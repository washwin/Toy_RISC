	.data
n:
	10
	.text
main:
	load %x0, $n, %x3
	addi %x5, 1, %x5
	addi %x0, 65535, %x7
	store %x4, 0, %x7
	beq %x3, 1, finish
	subi %x7, 1, %x7
	store %x5, 0, %x7
	beq %x3, 2, finish
	subi %x3, 2, %x3
	jmp loop
loop:
	add %x4, %x5, %x6
	store %x6, 0, %x7
	beq %x3, 1, finish
	subi %x3, 1, %x3
	addi %x5, 0, %x4
	addi %x6, 0, %x5
	jmp loop
finish:
	end
