package me.barnabbas.mercator.server.area

/**
 * This is a message used to indicate through what exit or entrance the Entity is entering
 */
trait Gate

/**
 * Contains some default Gates that are used by many areas
 */
object Gate {
  /**
   * Indicating that the Entity left through a log out
   */
  case object LogOut extends Gate
  /**
   * Indicating that the Entity enters through a log in
   */
  case object LogIn extends Gate
  /**
   * Indicating the the Entity enters through his first Log in.
   * This is called when an account got created.
   */
  case object FirstLogIn extends Gate
}