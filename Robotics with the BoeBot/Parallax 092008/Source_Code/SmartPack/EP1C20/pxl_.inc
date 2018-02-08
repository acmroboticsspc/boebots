function pxl_ (clk, px[2..0])
	with (a_exp, d_exp)
	returns (load, w, a[(a_exp) - (1)..0], d[((2) ^ (d_exp)) - (1)..0]);
