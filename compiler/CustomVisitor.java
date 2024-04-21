package compiler;

import java.util.HashMap;
import org.objectweb.asm.*;

/**
 * This class overrides the methods in the antlr-generated BaseVisitor class to traverse a parse tree
 * 
 * @author Kirin Sharma
 * @version 1.0
 * Assignment 5
 * CS322 - Compiler Construction
 * Spring 2024
 */

public class CustomVisitor<T> extends lexparse.KnightCodeBaseVisitor<T>
{
    
    public HashMap<String, Variable> symbolTable; // the symbol table for all variables
    public int indexCount; // keeps track of current memory location for variable assignment
    public String outputName; // the name of the output file
    public ClassWriter cw; // the ASM class writer used in various methods to write bytecode
    public MethodVisitor mv; // the ASM method visitor used in various methods to write bytecode for the main method

    /**
     * Preferred constructor initializes filename and other field variables to default values
     */
    public CustomVisitor(String fileName)
    {
        symbolTable = new HashMap<String, Variable>(); 
        indexCount = 0;
        outputName = fileName;
    } // end preferred constructor

    
    @Override
    /**
     * Contains ASM code to create the class and main method of the .class file
     */
    public T visitFile(lexparse.KnightCodeParser.FileContext ctx) 
    {
        cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        cw.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC, outputName, null, "java/lang/Object",null);
        mv = cw.visitMethod(Opcodes.ACC_PUBLIC+Opcodes.ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);
        mv.visitCode();
        return visitChildren(ctx); 
    } // end visitFile


    @Override
    /**
     * Contains logic to declare a new variable in the symbol table
     * Assigns its type and a memory location for later use
     */
    public T visitVariable(lexparse.KnightCodeParser.VariableContext ctx) 
    {
        String dataType = ctx.getChild(0).getText(); // gets the left grandchild of the variable subtree, which is the data type
        String identifier = ctx.getChild(1).getText(); // gets the right grandchild of the variable subtree, which is the name of the identifier
        
        if(dataType.equals("INTEGER"))
        {
            symbolTable.put(identifier, new Variable("Integer", indexCount)); // declares a new Integer in the symbol table with a free memory location
            indexCount++; 
        }
        else if(dataType.equals("STRING"))
        {
            symbolTable.put(identifier, new Variable("String", indexCount)); // declares a new String in the symbol table with a free memory location
            indexCount++; 
        }
        
        return visitChildren(ctx); 
    } // end visitVariable


    @Override
    /**
     * Contains ASM code to assign a value to a variable
     */
    public T visitSetvar(lexparse.KnightCodeParser.SetvarContext ctx)
    {
        String identifier = ctx.getChild(1).getText();

        if(symbolTable.get(identifier).getDataType().equals("Integer"))
        {
            String val = ctx.getChild(3).getText();

            // If val is not a single integer, visit children and perform necessary operations before storing result
            if(ctx.getChild(3).getChildCount() != 1)
            {
                visit(ctx.getChild(3));
                mv.visitVarInsn(Opcodes.ISTORE, symbolTable.get(identifier).getMemoryLocation());
            }
            else 
            {
                // Load integer into memory
                Integer value = Integer.parseInt(val);
                mv.visitLdcInsn(value);
                mv.visitVarInsn(Opcodes.ISTORE, symbolTable.get(identifier).getMemoryLocation());
            }
        }
        else
        {
            // Load String into memory 
            String value = ctx.getChild(3).getText();
            value = value.replace("\"", ""); // remove quotation marks from string
            mv.visitLdcInsn(value);
            mv.visitVarInsn(Opcodes.ASTORE, symbolTable.get(identifier).getMemoryLocation());
        }

        return null;
    } // end visitSetVar


    @Override
    /**
     * Contains ASM code to print either a variable or a string literal
     */
    public T visitPrint(lexparse.KnightCodeParser.PrintContext ctx)
    {
        String identifier = ctx.getChild(1).getText();

        // Check if what is being printed is a string literal
        if(!symbolTable.containsKey(identifier))
        {
            // Print a String literal
            identifier = identifier.replace("\"", ""); // remove quotation marks from string
            mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv.visitLdcInsn(identifier);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
            return visitChildren(ctx);
        }

        // Prints a variable
        if(symbolTable.get(identifier).getDataType().equals("Integer"))
        {
            // Print an integer variable
            mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv.visitVarInsn(Opcodes.ILOAD, symbolTable.get(identifier).getMemoryLocation());
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(I)V", false);
        }
        else
        {
            // Print a String variable
            mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv.visitVarInsn(Opcodes.ALOAD, symbolTable.get(identifier).getMemoryLocation());
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
        }
        
        return visitChildren(ctx);
    } // end visitPrint


    @Override
    /** 
     * Contains logic to add numbers in bytecode, including logic to handle multiple additions
     * Utilizes a postorder traversal
     */ 
    public T visitAddition(lexparse.KnightCodeParser.AdditionContext ctx) 
    { 
        postorderTraversal(ctx); // call to helper method using postorder traversal to load two operands onto stack

        mv.visitInsn(Opcodes.IADD); // add two operands and have result on top of stack
        return null;
    } // end visitAddition


    @Override
    /**
     * Contains logic to multiply two integers in bytecode, including logic to handle multiple multiplications
     * Utilizes a postorder traversal
     */
    public T visitMultiplication(lexparse.KnightCodeParser.MultiplicationContext ctx) 
    {
        postorderTraversal(ctx); // call to helper method using postorder traversal to load two operands onto stack

        mv.visitInsn(Opcodes.IMUL); // multiply two operands and have result on top of stack
        return null;
    } // end visitMultiplication


    @Override
    /**
     * Contains logic to subtract two integers, in bytecode including logic to handle multiple subtractions
     * Utilizes a postorder traversal
     */
    public T visitSubtraction(lexparse.KnightCodeParser.SubtractionContext ctx)
    {
        postorderTraversal(ctx); // call to helper method using postorder traversal to load two operands onto stack

        mv.visitInsn(Opcodes.ISUB); // subtract two operands and have result on top of stack
        return null;
    } // end visitSubtraction


    @Override
    /**
     * Contains logic to divide two integers in bytecode, including logic to handle multiple divisions
     * Utilizes a postorder traversal
     */
    public T visitDivision(lexparse.KnightCodeParser.DivisionContext ctx)
    {
        postorderTraversal(ctx); // call to helper method using postorder traversal to load two operands onto stack

        mv.visitInsn(Opcodes.IDIV); // subtract two operands and have result on top of stack
        return null;
    } // end visitDivision


    @Override 
    /**
     * Contains logic to write bytecode to read in an integer or string value to a variable
     */
    public T visitRead(lexparse.KnightCodeParser.ReadContext ctx) 
    {
        // Initialize and store scanner object if there is not already one
        if(!symbolTable.containsKey("Scanner"))
        {
            symbolTable.put("Scanner", new Variable("SCANNER", indexCount));
            mv.visitTypeInsn(Opcodes.NEW, "java/util/Scanner");
            mv.visitInsn(Opcodes.DUP);
            mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "in", "Ljava/io/InputStream;"); // load System.in onto stack
            mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/util/Scanner", "<init>", "(Ljava/io/InputStream;)V", false);
            mv.visitVarInsn(Opcodes.ASTORE, indexCount);
            indexCount++;
        }
        
        // Load scanner onto stack
        mv.visitVarInsn(Opcodes.ALOAD, symbolTable.get("Scanner").getMemoryLocation());

        // Get the next string or int depending on what the variable type we are reading into is
        Variable var = symbolTable.get(ctx.getChild(1).getText());
        if(var.getDataType().equals("String"))
        {
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/util/Scanner", "nextLine", "()Ljava/lang/String;", false);
            mv.visitVarInsn(Opcodes.ASTORE, var.getMemoryLocation());
        }
        else if(var.getDataType().equals("Integer"))
        {
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/util/Scanner", "nextInt", "()I", false);
            mv.visitVarInsn(Opcodes.ISTORE, var.getMemoryLocation());
        }

        return visitChildren(ctx); 
    } // end visitRead


    @Override 
    /**
     * Contains logic to compile an if else statement to bytecode using ASM
     */
    public T visitDecision(lexparse.KnightCodeParser.DecisionContext ctx) 
    {
        // Load 2 values to be compared onto stack
        String term1 = ctx.getChild(1).getText();
        String term2 = ctx.getChild(3).getText();
        loadInteger(term1);
        loadInteger(term2);                 
        
        Label falseLabel = new Label(); // label for if comparison output is false
        Label endLabel = new Label(); // label for the end of the comparison

        // Determine correct comparison operation and write to class
        String comparator = ctx.getChild(2).getText();
        writeCompareOperation(falseLabel, comparator); // call to helper method to write the correct comparison operation

        int childIndex = 5; // the fifth child is the first actual operation to do following comparison
        while(!ctx.getChild(childIndex).getText().equals("ELSE"))
        {
            visit(ctx.getChild(childIndex)); // visit the child if it is not the else statement
            childIndex++;
        }
        mv.visitJumpInsn(Opcodes.GOTO, endLabel); // skip over what you would do in the else statemtne and go to the endLabel

        mv.visitLabel(falseLabel);
        // If there is an else statement following, visit what you would do otherwise
        if(ctx.getChild(childIndex).getText().equals("ELSE"))
        {
            childIndex++;
            while(!ctx.getChild(childIndex).getText().equals("ENDIF"))
            {
                visit(ctx.getChild(childIndex)); // visit the child if it is not the endif statement
                childIndex++;
            }
        }

        mv.visitLabel(endLabel); // Label indicating end of comparison
        return null; 
    } // end visitDecision


    @Override
    /**
     * Override of the visitLoop method contains logic to implement a while loop in bytecode using ASM
     */
    public T visitLoop(lexparse.KnightCodeParser.LoopContext ctx) 
    {    
        Label loopHeader = new Label();
        Label loopEnd = new Label();

        mv.visitLabel(loopHeader);
        // Load 2 values to be compared onto stack
        String term1 = ctx.getChild(1).getText();
        String term2 = ctx.getChild(3).getText();
        loadInteger(term1);
        loadInteger(term2);

        // Determine correct comparison operation and write to class
        String comparator = ctx.getChild(2).getText();
        writeCompareOperation(loopEnd, comparator); // call to helper method to write the correct comparison in bytecode

        visitChildren(ctx);
        mv.visitJumpInsn(Opcodes.GOTO, loopHeader); // if it gets here, then loop to top and do comparison again

        mv.visitLabel(loopEnd); // Label to visit when the loop should no longer run
        return null;
    } // end visitLoop


    /**
     * Helper function writes the correct comparison operation to the .class file assuming the two operands are loaded onto stack already
     * @param falseLabel the label to visit if the comparison returns false
     * @param comparator the string representing the comparison operator
     */
    private void writeCompareOperation(Label falseLabel, String comparator)
    {
        // Inverse logic to go to the falselabel if the comparison is false
        if(comparator.equals(">"))
        {
            mv.visitJumpInsn(Opcodes.IF_ICMPLE, falseLabel);
        }
        else if(comparator.equals("<"))
        {
            mv.visitJumpInsn(Opcodes.IF_ICMPGE, falseLabel);
        }
        else if(comparator.equals("="))
        {
            mv.visitJumpInsn(Opcodes.IF_ICMPNE, falseLabel);
        }
        else if(comparator.equals("<>"))
        {
            mv.visitJumpInsn(Opcodes.IF_ICMPEQ, falseLabel);
        }
    } // end writeCompareOperation


    /**
     * Helper method to load either an explicit integer onto the stack
     * or the value of an integer variable onto the stack
     * @param term the string representing the term; could be an identifier for an integer variable or a literal integer
     */
    private void loadInteger(String term)
    {
        // Determine if the term is an explicit integer or a variable and load it onto the stack
        if(!symbolTable.containsKey(term))
        {
            mv.visitLdcInsn(Integer.parseInt(term)); // load an explicit integer onto stack
        }
        else
        {
            mv.visitVarInsn(Opcodes.ILOAD, symbolTable.get(term).getMemoryLocation()); // load a variable value onto stack
        }
        return;
    } // end loadInteger
    

    /**
     * Helper method that loads two operands onto the stack using a postorder traversal
     * @param ctx the node to start the postorder traversal at
     */
    private void postorderTraversal(org.antlr.v4.runtime.ParserRuleContext ctx)
    {
        // Load the first operand onto the stack (left subtree traversal)
        if(ctx.getChild(0).getChildCount() == 1)
        {
            String operand1 = ctx.getChild(0).getText();
            loadInteger(operand1); // Check if first operand is a variable or literal integer and load onto stack
        }
        else 
        {
            visit(ctx.getChild(0)); // visits the left child recursively
        }

        // Load the second operand onto the stack (right subtree traversal)
        if(ctx.getChild(2).getChildCount() == 1)
        {
            String operand2 = ctx.getChild(2).getText();
            loadInteger(operand2); // Check if second operand is a variable or literal integer and load onto stack
        }
        else 
        {
            visit(ctx.getChild(2)); // visits right child recursively
        }
        
        return;
    } // end postorderTraversal


    /**
     * Contains ASM code to write the footer of a .class file
     */
    public void writeFooter()
    {
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(0,10);
        mv.visitEnd();
        return;
    } // end writeFooter


    /**
     * Returns the ClassWriter as an array of bytes
     * @return
     */
    public byte[] getByteArray()
    {
        return cw.toByteArray();
    } // end getByteArray

} // end class
