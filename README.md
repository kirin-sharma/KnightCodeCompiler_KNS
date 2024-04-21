# KnightCodeCompiler

## Overview
This program is a tool designed to compile programs written in the KnightCode Language (grammar file attached) into Java bytecode.

## Features
- Converts KnightCode (.kc) source files into Java bytecode (.class) files. 
- Suports basic programming constructs such as loops, conditionals, variables, and arithmetic. Works with only strings and integers.
- Generates Java bytecode that is compatible with and executable by the Java Virtual Machine

## Usage
After cloning the repository to your local machine, one must ensure that they have the required ASM and ANTLR packages installed.
Once they have been downloaded, run the following command: ant build-grammar compile-grammar compile
This will create the necessary antlr resources that the compiler is dependent on based on the grammar file.
Once these commands have been executed, the following directions will allow you to use the compiler:
The file kcc.java, located in the compiler directory, is what is utilized to kick off the compilation process.
The interface for this compiler is the command line with two command line arguments: the knightcode file location, and the output file name.
For example: java compiler/kcc tests/program1.kc output/Program1
This would create a .class file named Program1.class in the output directory which could then be executed by java output/Program1

## License

MIT © Kirin Sharma
