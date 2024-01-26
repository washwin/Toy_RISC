	.data
a:
	1
	.text
main:
	load %x0, $a, $x3
	beq %x0, 1, notprime
	subi %x3, 1, %x4
	addi %x8, 1, %x7
	jmp loop
loop:
	div %x3, %x4, %x5
	mul %x5, %x4, %x6
	beq %x3, %x6, notprime
	subi %x4, 1, %x4
	beq %x4, %x7, prime
	jmp loop
notprime:
	subi %x8, 1, %x10
	end
prime:
	addi %x8, 1, %x10
	end