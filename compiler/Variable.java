package compiler;

/**
 * This class represents a KnightCode variable to be stored in the symbol table.
 * Supports only Strings and Integers
 * 
 * @author Kirin Sharma
 * @version 1.0
 * Assignment 5
 * CS322 - Compiler Construction
 * Spring 2024
 */

public class Variable
{

    public String dataType; // a string denoting the data type of the variable
    public int memoryLocation; // the memory location of the variable in bytecode

    /**
     * Preferred constructor initializes a variable, allowing user to pass in a datatype and memory location
     * @param type a string denoting the dataType of the variable
     * @param memoryLocation the memory index of the variable in the bytecode
     */
    public Variable(String type, int memoryLocation)
    {
        dataType = type;
        this.memoryLocation = memoryLocation;
    } // end preferred constructor

    /**
     * Getter for dataType
     * @return the string denoting the data type of the variable
     */
    public String getDataType()
    {
        return dataType;
    } // end getDataType

    /**
     * Getter for memoryLocation
     * @return the memory location of the variable in the bytecode
     */
    public int getMemoryLocation()
    {
        return memoryLocation;
    } // end getMemoryLocation

} // end class
