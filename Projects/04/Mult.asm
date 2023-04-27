// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/04/Mult.asm

// Multiplies R0 and R1 and stores the result in R2.
// (R0, R1, R2 refer to RAM[0], RAM[1], and RAM[2], respectively.)
//
// This program only needs to handle arguments that satisfy
// R0 >= 0, R1 >= 0, and R0*R1 < 32768.

@0     //check if R[0] == 0
D=M
@IF1
D;JEQ 
     
@1     //check if R[1] == 0  
D=M
@IF0
D;JEQ

@i      
M = 0   //RESET R[i]

(LOOP)
@0      
D=M     
@i    
M=M+D   
@1      
M=M-1   
D=M     
@END
D;JEQ
@LOOP
D;JGT

(IF0)    //RAM[0]==0 || RAM[1]==0
@i
M=0
@END
0;JMP

(END)    // load product to RAM[2]
@i
D=M
@2
M=D
