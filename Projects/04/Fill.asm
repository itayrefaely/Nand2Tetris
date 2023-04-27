// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/04/Fill.asm

// Runs an infinite loop that listens to the keyboard input.
// When a key is pressed (any key), the program blackens the screen,
// i.e. writes "black" in every pixel;
// the screen should remain fully black as long as the key is pressed. 
// When no key is pressed, the program clears the screen, i.e. writes
// "white" in every pixel;
// the screen should remain fully clear as long as no key is pressed.

// load screen into pointer R0
@SCREEN
D=A
@0
M=D     

// if key is not pressed go to white, else go to black
(LOOP)
@8191 // reset i to last row index
D=A
@i
M=D
@KBD
D=M
@WHITE
D;JEQ
@BLACK
D;JMP

// set all pixels to white
(WHITE)
@R0
D=M
@i
A=D+M
M=0
@i
M=M-1
@i
D=M
@LOOP // check if all 8192 rows have been set to 0, if so -> return to LOOP
D;JLT
@WHITE // not all rows done, continue to next row by entering WHITE loop again
0;JMP

// set all pixels to black
(BLACK)
@R0
D=M
@i
A=D+M
M=-1
@i
M=M-1
@i
D=M
@LOOP // check if all 8192 rows have been set to -1, if so -> return to LOOP
D;JLT
@BLACK // not all rows done, continue to next row by entering BLACK loop again
0;JMP


