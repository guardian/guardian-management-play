package com.gu.management

case class UserCredentials(username: String, password: String)
trait UserProvider {
  def realm: String
  def isValid(credentials: UserCredentials): Boolean
}