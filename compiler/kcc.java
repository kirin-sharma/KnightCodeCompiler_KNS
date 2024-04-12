package compiler;

import java.io.IOException;
//ANTLR packages
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import org.antlr.v4.gui.Trees;

import lexparse.*;

/**
 * This class contains the main method which compiles a given kc file to a specified output location
 * 
 * @author Kirin Sharma
 * @version 1.0
 * Assignment 5
 * CS322 - Compiler Construction
 * Spring 2024
 */

 public class kcc
 {

    public static void main(String[] args) 
    {
        
        CharStream input;
        KnightCodeLexer lexer;
        CommonTokenStream tokens;
        KnightCodeParser parser;

        try
        {
            input = CharStreams.fromFileName(args[0]);  //get the input
            lexer = new KnightCodeLexer(input); //create the lexer
            tokens = new CommonTokenStream(lexer); //create the token stream
            parser = new KnightCodeParser(tokens); //create the parser
            String outputName = args[1]; // the name of the output file read from command line arguments

            ParseTree tree = parser.file();  //set the start location of the parser
            Trees.inspect(tree, parser);  //displays the parse tree
            
            CustomVisitor<Void> visitor = new CustomVisitor<Void>(outputName); // Creates a new CustomVisitor
            visitor.visit(tree); // Traverses tree and writes bytecode
            visitor.writeFooter(); // Call to helper method to wrtite footer of .class file

            // Write bytes created by above code to the specified output file; writeFile credit: Dr. Robert Kelly
            byte[] b = visitor.getByteArray();
            Utilities.writeFile(b, outputName + ".class");

        }
        catch(IOException e)
        {
            System.out.println(e.getMessage());
        }

    } // end main


 } // end class
 