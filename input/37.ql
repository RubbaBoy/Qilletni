
int a = 2 + 1
print("a = %d  [%d]".format([a, 3]))

int b = 2 * 3
print("b =  %d  [%d]".format([b, 6]))

double c = 6 / 2
print("c = %f  [%f]".format([c, 3.0]))

boolean d = 6 > 2
print("d = %b  [%b]".format([d, true]))

boolean e = 6 < 2
print("e = %b  [%b]".format([e, false]))

boolean f = 6 >= 2
print("f = %b  [%b]".format([f, true]))

boolean g = 6 <= 2
print("g = %b  [%b]".format([g, false]))

boolean h1 = 6 == 2
print("h1 = %b  [%b]".format([h1, false]))

boolean i1 = 6 != 2
print("i1 = %b  [%b]".format([i1, true]))

boolean j1 = 6 == 6
print("j1 = %b  [%b]".format([j1, true]))

boolean k1 = 6 != 6
print("k1 = %b  [%b]".format([k1, false]))

int h = 2 + 1
print("h = %d  [%d]".format([h, 3]))

int h_mod = 7 % 3
print("h_mod = %d  [%d]".format([h_mod, 1]))

int h_div = 7 /~ 3
print("h_div = %d  [%d]".format([h_div, 2]))

int i = 2 * 3
print("i = %d  [%d]".format([i, 6]))

int i_mod = 8 % 3
print("i_mod = %d  [%d]".format([i_mod, 2]))

int i_div = 8 /~ 3
print("i_div = %d  [%d]".format([i_div, 2]))

double j = 6 / 2
print("j = %f  [%f]".format([j, 3.0]))

int j_mod = 9 % 4
print("j_mod = %d  [%d]".format([j_mod, 1]))

int j_div = 9 /~ 4
print("j_div = %d  [%d]".format([j_div, 2]))

boolean k = (6 > 2) && (6 < 2)
print("k = %b  [%b]".format([k, false]))

boolean l = (6 > 2) || (6 < 2)
print("l = %b  [%b]".format([l, true]))

boolean m = (6 > 2) && (6 < 2) || (6 == 6)
print("m = %b  [%b]".format([m, true]))

boolean n = !(6 > 2)
print("n = %b  [%b]".format([n, false]))

int[] o = [1, 2, 3, 4, 5]

int p = o[1]
print("p = %d  [%d]".format([p, 2]))

int q_a = 2
int q = o[1 + q_a]
print("q = %d  [%d]".format([q, 4]))

o[0]++

print("o[0] = %d  [%d]".format([o[0], 2]))

o[0] += 2

print("o[0] = %d  [%d]".format([o[0], 4]))
