	.data
a:
	11
	.text
main:
	load %x0, $a, %x3
	divi %x3, 2, %x4
	muli %x4, 2, %x5
	beq %x3, %x5, odd
	addi %x6, 1, %x10
	end
odd:
	subi %x6, 1, %x10
	end