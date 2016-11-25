package scalar
import scala.collection.mutable.ArrayBuffer
import TypeUtils._

class ScalaR {
	def NA = new NAType
	var variableMappings: Map[Symbol, RVector] = Map[Symbol, RVector]()

	implicit class VariableWrapper(s: Symbol) {

		def apply(idx: Int): Any = {
			val vec = variableMappings(s)
			return vec(idx-1).storedValue
		}

		def <--(value: Any) = {
			val buf = ArrayBuffer[Type]()
			value match {
				case b: Boolean  => buf += new Logical(b)
								    var vec = new RVector(buf, "Logical")
								    variableMappings += (s -> vec)
				case i: Int      => buf += new Numeric(i)
								    var vec = new RVector(buf, "Numeric")
								    variableMappings += (s -> vec)
				case d: Double   => buf += new Numeric(d)
								    var vec = new RVector(buf, "Numeric")
								    variableMappings += (s -> vec)
				case str: String => buf += new Character(str)
								    var vec = new RVector(buf, "Character")
								    variableMappings += (s -> vec)
				case v: RVector  => variableMappings += (s -> v)	
			}
		}

		def <--(variable: Symbol) = { 
			if (variableMappings.contains(variable)) {
				variableMappings += (s -> variableMappings(variable))
			} else {
				val name = variable.name
				throw new RuntimeException(s"Error: object '$name' not found")
			}
		}

		// def ==(value: Type) = {
		// 	if (variableMappings.contains(s) && variableMappings(s).getType == value.getType) {
		// 		value.storedValue == variableMappings(s).storedValue
		// 	} else if (!variableMappings.contains(s)) {
		// 		val name = s.name
		// 		throw new RuntimeException(s"Error: object '$name' not found")
		// 	} else {
		// 		throw new RuntimeException("Error: input objects must have same type")
		// 	}
		// }

		// def ==(value: Symbol) = {
		// 	if (variableMappings.contains(s) && variableMappings.contains(value) && variableMappings(s).getType == variableMappings(value).getType) {
		// 		variableMappings(value).storedValue == variableMappings(s).storedValue
		// 	} else if (!variableMappings.contains(s)) {
		// 		val name = s.name
		// 		throw new RuntimeException(s"Error: object '$name' not found")
		// 	} else if (!variableMappings.contains(value)) {
		// 		val name = value.name
		// 		throw new RuntimeException(s"Error: object '$name' not found")
		// 	} else {
		// 		throw new RuntimeException("Error: input objects must have same type")
		// 	}
		// }
	}

	def c(values: Any*): RVector = {
		val typeHierarchy = Array("Logical", "Integer", "Numeric", "Character")
		var highestType: String = "Logical"

		for (v <- values) {
			val curType = v match {
				case n: NAType  => "NA"
				case b: Boolean => "Logical"
				case i: Int     => "Numeric"
				case d: Double  => "Numeric"
				case s: String  => "String"
				case _          => "Unsupported Type"
			}

			if (curType == "Unsupported Type") 
				throw new IllegalArgumentException(s"Unsupported type: ${v.toString}")
			
			val curIdx = typeHierarchy.indexOf(curType)
			if (curIdx > typeHierarchy.indexOf(highestType)) {
				highestType = curType
			}
		}

		var buf = ArrayBuffer[Type]()
		return highestType match {
			case "Logical" => new RVector(buf ++ values.map(toLogical), "Logical")
			case "Numeric" => new RVector(buf ++ values.map(toNumeric), "Numeric")
			case "Character" => new RVector(buf ++ values.map(toCharacter), "Character")
		}
	}

	def length(s: Symbol): Int = variableMappings(s).length
	def typeOf(s: Symbol): String = variableMappings(s).getType

	// def asLogical(vec: RVector): RVector = {
	// 	var buf = ArrayBuffer[Type]() ++ vec.data.map(toLogical)
	// 	return new RVector(buf, "Logical")
	// }

	// def asNumeric(vec: RVector): RVector = {
	// 	var buf = ArrayBuffer[Type]() ++ vec.data.map(toNumeric)
	// 	return new RVector(buf, "Numeric")
	// }
	// def asCharacter(vec: RVector): RVector = {
	// 	var buf = ArrayBuffer[Type]() ++ vec.data.map(toCharacter)
	// 	return new RVector(buf, "Character")
	// }

	def getVariableMappings = variableMappings
}