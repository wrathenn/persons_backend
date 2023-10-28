package wrathenn.persons.model

sealed class ApiException(msg: String, cause: Option[Throwable])
  extends Exception(msg, cause.orNull)

case class NotFoundException(msg: String, cause: Option[Throwable] = None)
  extends ApiException(msg, cause)
  
case class BadArgumentException(msg: String, cause: Option[Throwable] = None)
  extends ApiException(msg, cause) 
