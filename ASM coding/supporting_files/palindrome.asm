	.data
a:
	121
	.text
main:
	load %x0, $a, %x3
	addi %x3, 0, %x4
	addi %x0, 0, %x5
	bgt %x0, %x3, check
loop:
	divi %x4, 10, %x4
	muli %x5, 10, %x5
	add %x5, %x31, %x5
	beq %x0, %x4, check
	jmp loop
check:
	beq %x3, %x5, palindrome
	subi %x0, 1, %x10
	end
palindrome:
	addi %x0, 1, %x10
	end