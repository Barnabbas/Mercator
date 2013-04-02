package me.barnabbas.mercator.server.view.components

import CommandView._
import akka.actor.ActorRef
import me.barnabbas.mercator.networking.Description
import me.barnabbas.mercator.networking.Messages
import me.barnabbas.mercator.server.view.ViewComponentFactory
import me.barnabbas.mercator.server.view.ViewComponent
import me.barnabbas.mercator.server.view.ViewComponentActor

/**
 * CommandView is a ViewComponent that allows text-based game behaviour.
 * CommandView uses commands to manage what it can use and what it can't use.<br>
 * It communicates with a Controller. The Controller has to support the following functionality:
 * It must handle commands send: those are the Commands that the user has entered.
 * It must send the current description of the situation and the new collection of commands.
 * It must send an Open message to allow this View again for listening to Commands.
 *
 * @author Barnabbas
 *
 */
class CommandView(controller: CommandController) extends ViewComponent {
  
  import Messages.CommandView._

  /**
   * The commands that are enabled at the moment
   */
  private var commands: Seq[Command] = _

  /**
   * Activating the Controller
   */
  override def setup = {
    val commandState = controller.start()
    commands = commandState.commands
    client ! commandState.description
    if (commandState.allowOpen) client ! Open
  }

  override def clientEvent = {

    // asked for help
    case "help" => {
      client ! helpMessage
      client ! Open
    }

    // any other command
    case CommandFinder(command) => commandReceived(command)

    // a wrong string
    case string: String => {
      client ! UNKNOWN_COMMAND
      client ! Open
    }

    // using auto-complete
    case Complete(prefix) => commandsStartWith(prefix) match {
      case Seq() => {
        client ! unknownPrefix(prefix)
        client ! Open
      }
      case Seq(command) => {
        client ! command.identifier
        commandReceived(command)
      }
      case Seq(commands @ _*) =>
        commands map (_.identifier) mkString "\n"
        client ! Open
    }
  }

  /**
   * Will handle the actions required when a command has been chosen
   */
  private def commandReceived(command: Command) = {
    val commandState = controller fire command
    commands = commandState.commands
    client ! commandState.description
    if (commandState.allowOpen) client ! Open
  }

  /**
   * The current help message
   */
  private def helpMessage = {
    (for (command <- commands) yield {
      s"${command.identifier} \t ${command.description} \n"
    }).mkString
  }

  /**
   * The commands that are currently enabled that start with {@code prefix}
   */
  private def commandsStartWith(prefix: String) = commands filter (_.identifier startsWith prefix)

  /**
   * A small extractor that turns an identifier into a command
   */
  private object CommandFinder {
    def unapply(string: String) = commands find (_.identifier == string)
  }

}

object CommandView {
  /**
   * A Command is an action an user can do.
   * @param identifier the name of this command, must be typed by the user
   * @param description a description of this Command
   */
  case class Command(identifier: String, description: String) extends Ordered[Command] {
    override def compare(c: Command) = identifier compare c.identifier
    override def toString = identifier
  }

  /**
   * Information used to indicate what the state of the CommandView is
   */
  class CommandState(val description: String, val commands: Seq[Command], val allowOpen: Boolean = true)

  /**
   * A trait that is used as a Controller for the CommandView.
   * The CommandController will be notified when a new Command has been called and must return his new state.
   */
  trait CommandController {
    /** Starts the CommandController. Should only be called once */
    def start(): CommandState
    /** Fires the given command and returns the new state of the CommandController */
    def fire(command: Command): CommandState
  }

  /**
   * Creates a ViewComponentFactory for the CommandViews with the given creator for the controller that will Control the TextView.
   * @param controller A function to get the controller for this CommandView
   */
  def apply[E](controller: E => CommandController): ViewComponentFactory[E] = ViewComponentFactory(Description.Text) { entity =>
    new CommandView(controller(entity)) with ViewComponentActor
  }

  private val UNKNOWN_COMMAND = "Unknown command; try \"help\" for more information"

  private def unknownPrefix(prefix: String) = s"""No command starting with "$prefix"; try "help" for more information"""
}