	.data
a:
	70
	80
	40
	20
	10
	30
	50
	60
n:
	8
	.text
main:
	load %x0, $n, %x3
	subi %x0, 1, %x4
	subi %x3, 1, %x6
loop1:
	addi %x4, 1, %x4
	addi %x4, 1, %x5
	beq %x4, %x6, out
loop2:
	beq %x5, %x3, loop1
	load %x4, $a, %x10
	load %x5, $a, %x11
	blt %x10, %x11, swap
	addi %x5, 1, %x5
	jmp loop2
swap:
	store %x10, $a, %x5	
	store %x11, $a, %x4
	jmp loop2
out:
	end