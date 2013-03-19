package me.barnabbas.mercator.client.networking

import me.barnabbas.mercator.networking.AreaSet
import me.barnabbas.mercator.networking.Messages.Update
import me.barnabbas.mercator.networking.Description
import me.barnabbas.mercator.networking.Description.SideScrolling3D
import me.barnabbas.mercator.client.view.sidescrolling.areas._

class SideScrollingNetworker extends NetworkerComponent {

  private var _areaSet = Option.empty[AreaSet]

  override def receiveMessage = {
    case areaSet: AreaSet => {
      _areaSet = Some(areaSet)
    }
  }
  
  override def renderer(description: Description) = {
    val SideScrolling3D(identifier) = description
    identifier match{
      case "welcome.field" => new WelcomeFieldRenderer(this)
      case "welcome.house" => new WelcomeHouseRenderer(this)
    }
  }
  
  /**
   * Makes this networker update.<br>
   * Note this is not directly, it uses the server to update.
   */
  def update() = server ! Update
  
  def areaSet = _areaSet
}