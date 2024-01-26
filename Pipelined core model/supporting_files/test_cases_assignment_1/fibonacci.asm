	.data
n:
	3
	.text
main:
	load %x0, $n, %x3
    beq %x0, %x3, finish
    addi %x0, 0, %x4
	addi %x0, 1, %x5
	addi %x0, 65535, %x7
	store %x4, 0, %x7
	subi %x7, 1, %x7
    subi %x3, 1, %x3
	beq %x0, %x3, finish
	store %x5, 0, %x7
	subi %x7, 1, %x7
    subi %x3, 1, %x3
	beq %x0, %x3, finish
	jmp loop
loop:
	beq %x3, %x0, finish
	subi %x3, 1, %x3
	add %x4, %x5, %x6
	store %x6, 0, %x7
    subi %x7, 1, %x7
	addi %x5, 0, %x4
	addi %x6, 0, %x5
	jmp loop
finish:
	end