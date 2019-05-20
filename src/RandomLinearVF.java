
import java.util.Random;

/**
 * 1) RNG-Vectors are assigned a random magnitude between 0 and MAG_MAX.
 * <br>
 * 2) The creation algorithm starts off by checkering the vector field with vectors of random direction and<br>
 * magnitude every RNG_SPACING across the vector feild in both x and y directions.<br>
 * <br>
 * 3) The creation algorithm then smooths the vf by using the rng vectors to fill in the rest of the vf.<br>
 * In other words, the non-rng vectors do not affect eachother.
 * <br>
 * @author Travis Pavelka
 *
 */
public class RandomLinearVF {
	public static final int MAG_MAX = 30;
	
	private int RNG_SPACING;
	
	/**
	 * The refrence to the random this vf uses
	 */
	private Random rand;
	public Random getRandom() {
		return this.rand;
	}
	
	/**
	 * The size of this randomized vf
	 */
	private int reqwidth;
	public int getWidth() {
		return this.reqwidth;
	}
	private int actwidth;
	
	private int reqheight;
	public int getHeight() {
		return this.reqheight;
	}
	private int actheight;
	
	/**
	 * A 2-D Array of the vectors, might be larger than width and height so that the
	 * generation algorithm can go all the way to the edges of the VF
	 */
	private Vector[][] vectors;
	public Vector getVector(int x, int y) {
		if(this.vectors[y][x] != null) {
			return this.vectors[y][x];
		} else {
			return null;
		}
	}
	private void setVector(int x, int y, Vector v) {
		this.vectors[y][x] = v;
	}
	
	/**
	 * These functions return the support vector in the specified direction
	 * on the closest divisibility line. If the given argument is on a
	 * divisibility line, it returns the argument.
	 */
	private int getAbove(int cury) {
		int rem = cury % RNG_SPACING;
		int newy = cury - rem;
		return newy;
	}
	private int getBelow(int cury) {
		int rem = cury % RNG_SPACING;
		if(rem != 0) {
			int diff = RNG_SPACING - rem;
			int newx = cury + diff;
			return newx;
		} else {
			return cury;
		}
	}
	private int getLeft(int curx) {
		int rem = curx % RNG_SPACING;
		int newx = curx - rem;
		return newx;
	}
	private int getRight(int curx) {
		int rem = curx % RNG_SPACING;
		if(rem != 0) {
			int diff = RNG_SPACING - rem;
			int newx = curx + diff;
			return newx;
		} else {
			return curx;
		}
	}
	
	/**
	 * The generation algorithm may make the VF larger than requested in order to<br>
	 * properly generate the VF. The user will not have to worry about this though<br>
	 * as the VF will seem like it is the reqested size.<br>
	 * @param seed used by random to get a seeded vf
	 * @param width the requested width of the canvas
	 * @param height the requested height of the canvas
	 */
	public RandomLinearVF(long seed, int rng_spacing, int width, int height) {
		// initialization
		this.rand = new Random(seed);
		this.RNG_SPACING = rng_spacing;
		this.reqwidth = width;
		this.reqheight = height;
		
		// the vector field's width and height has to be divisible by the rng spacing,
		// so the array is set to the next highest divisible int
		
		// get the space between the end of the requested size and the next divisible int
		int addtowidth = 0;
		int addtoheight = 0;
		if(this.reqwidth % RNG_SPACING != 0) {
			addtowidth = RNG_SPACING - (this.reqwidth % RNG_SPACING);
		}
		if(this.reqheight % RNG_SPACING != 0) {
			addtoheight = RNG_SPACING - (this.reqheight % RNG_SPACING);
		}
		
		// calculate the actual width and height
		this.actwidth = this.reqwidth + addtowidth + 1;
		this.actheight = this.reqheight + addtoheight + 1;
		
		// initialize the vector array
		this.vectors = new Vector[this.actheight][this.actwidth];
		
		// iterate through rng positions
		// and set rng vectors
		for(int y = 0; y < this.actheight; y += RNG_SPACING) {
			for(int x = 0; x < this.actwidth; x += RNG_SPACING) {
				// add rng vector
				double direction = this.rand.nextDouble() * 360;
				double magnitude = this.rand.nextDouble() * MAG_MAX;
				Vector add = new Vector(direction, magnitude, Vector.DIR_MAG);
				add.setRNG(true);
				add.calcComps();
				this.setVector(x, y, add);
			}
		}
		// rng-vectors added
		
		// iterate through all vectors
		int curx = 0;
		int cury = 0;
		while(cury < this.actheight) {
			while(curx < this.actwidth) {
				// see if this vector has been calculated already
				if(this.getVector(curx,  cury) == null) {
					// this vector has not been calculated
					// see if this vector is on a divisibility line
					boolean xline = false;
					if(curx % RNG_SPACING == 0) {
						xline = true;
					}
					boolean yline = false;
					if(cury % RNG_SPACING == 0) {
						yline = true;
					}
					if(xline && !yline) {
						// this iteration is on a x divisibility line
						int abovex = curx;
						int abovey = this.getAbove(cury);
						
						int belowx = curx;
						int belowy = this.getBelow(cury);
						// calculate the solution vector's x component using point-slope
						Vector above = this.getVector(abovex, abovey);
						Vector below = this.getVector(belowx, belowy);
						// rise = change in x comp; run = change in y position
						double slopex = (double)(above.getX()-below.getX())/(double)(abovey-belowy);
						double solx = (slopex*(cury-belowy))+below.getX();
						// rise = change in y comp; run = change in y position
						double slopey = (double)(above.getY()-below.getY())/(double)(abovey-belowy);
						double soly = (slopey*(cury-belowy))+below.getY();
						// set the solution vector's components
						Vector sol = new Vector(solx, soly, Vector.COMPONENTS);
						this.setVector(curx, cury, sol);
						
					} else if(!xline && yline) {
						// this iteration is on a y divisibility line
						int leftx = this.getLeft(curx);
						int lefty = cury;
						
						int rightx = this.getRight(curx);
						int righty = cury;
						// calculate the solution vector's x component using point-slope
						Vector left = this.getVector(leftx, lefty);
						Vector right = this.getVector(rightx, righty);
						// rise = change in x comp; run = change in x position
						double slopex = (double)(right.getX()-left.getX())/(double)(rightx-leftx);
						double solx = (slopex*(curx-leftx))+left.getX();
						// rise = change in y comp; run = change in x position
						double slopey = (double)(right.getY()-left.getY())/(double)(rightx-leftx);
						double soly = (slopey*(curx-leftx))+left.getY();
						// set the solution vector's components
						Vector sol = new Vector(solx, soly, Vector.COMPONENTS);
						this.setVector(curx, cury, sol);
						
					} else if(!xline && !yline) {
						// this iteration is somewhere between four focal vectors
						int sabovex = curx;
						int sabovey = this.getAbove(cury);
						
						int sbelowx = curx;
						int sbelowy = this.getBelow(cury);
						
						int sleftx = this.getLeft(curx);
						int slefty = cury;
						
						int srightx = this.getRight(curx);
						int srighty = cury;
						// calculate support vectors if needed
						Vector sabove = this.getVector(sabovex, sabovey);
						if(sabove == null) {
							// sabove is on a y divisiblility line
							int saleftx = this.getLeft(sabovex);
							int salefty = sabovey;
							
							int sarightx = this.getRight(sabovex);
							int sarighty = sabovey;
							// calculate sabove's components
							int saleftdist = sabovex - saleftx;
							int sarightdist = sarightx - sabovex;
							
							Vector saleft = this.getVector(saleftx, salefty);
							Vector saright = this.getVector(sarightx, sarighty);

							double sasolx 	= (((double)(RNG_SPACING-saleftdist)/RNG_SPACING) * saleft.getX())
											+ (((double)(RNG_SPACING-sarightdist)/RNG_SPACING) * saright.getX());
							double sasoly 	= (((double)(RNG_SPACING-saleftdist)/RNG_SPACING) * saleft.getY())
											+ (((double)(RNG_SPACING-sarightdist)/RNG_SPACING) * saright.getY());
							// set sabove's components
							sabove = new Vector(sasolx, sasoly, Vector.COMPONENTS);
						}
						Vector sbelow = this.getVector(sbelowx, sbelowy);
						if(sbelow == null) {
							// sbelow is on a y divisiblility line
							int sbleftx = this.getLeft(sbelowx);
							int sblefty = sbelowy;
							
							int sbrightx = this.getRight(sbelowx);
							int sbrighty = sbelowy;
							// calculate sbelow's components
							int sbleftdist = sbelowx - sbleftx;
							int sbrightdist = sbrightx - sbelowx;
							
							Vector sbleft = this.getVector(sbleftx, sblefty);
							Vector sbright = this.getVector(sbrightx, sbrighty);

							double sbsolx 	= (((double)(RNG_SPACING-sbleftdist)/RNG_SPACING) * sbleft.getX())
											+ (((double)(RNG_SPACING-sbrightdist)/RNG_SPACING) * sbright.getX());
							double sbsoly 	= (((double)(RNG_SPACING-sbleftdist)/RNG_SPACING) * sbleft.getY())
											+ (((double)(RNG_SPACING-sbrightdist)/RNG_SPACING) * sbright.getY());
							// set sbelow's components
							sbelow = new Vector(sbsolx, sbsoly, Vector.COMPONENTS);
						}
						Vector sleft = this.getVector(sleftx, slefty);
						if(sleft == null) {
							// sleft is on a x divisiblility line
							int slabovex = sleftx;
							int slabovey = this.getAbove(slefty);
							
							int slbelowx = sleftx;
							int slbelowy = this.getBelow(slefty);
							// calculate sleft's components
							int slabovedist = slefty - slabovey;
							int slbelowdist = slbelowy - slefty;
							
							Vector slabove = this.getVector(slabovex, slabovey);
							Vector slbelow = this.getVector(slbelowx, slbelowy);
							
							double slsolx 	= (((double)(RNG_SPACING-slabovedist)/RNG_SPACING) * slabove.getX())
											+ (((double)(RNG_SPACING-slbelowdist)/RNG_SPACING) * slbelow.getX());
							double slsoly 	= (((double)(RNG_SPACING-slabovedist)/RNG_SPACING) * slabove.getY())
											+ (((double)(RNG_SPACING-slbelowdist)/RNG_SPACING) * slbelow.getY());
							// set sleft's components
							sleft = new Vector(slsolx, slsoly, Vector.COMPONENTS);
						}
						Vector sright = this.getVector(srightx, srighty);
						if(sright == null) {
							// sright is on a x divisiblility line
							int srabovex = srightx;
							int srabovey = this.getAbove(srighty);
							
							int srbelowx = srightx;
							int srbelowy = this.getBelow(srighty);
							// calculate sright's components
							int srabovedist = srighty - srabovey;
							int srbelowdist = srbelowy - srighty;
							
							Vector srabove = this.getVector(srabovex, srabovey);
							Vector srbelow = this.getVector(srbelowx, srbelowy);
							
							double srsolx 	= (((double)(RNG_SPACING-srabovedist)/RNG_SPACING) * srabove.getX())
											+ (((double)(RNG_SPACING-srbelowdist)/RNG_SPACING) * srbelow.getX());
							double srsoly 	= (((double)(RNG_SPACING-srabovedist)/RNG_SPACING) * srabove.getY())
											+ (((double)(RNG_SPACING-srbelowdist)/RNG_SPACING) * srbelow.getY());
							// set sright's components
							sright = new Vector(srsolx, srsoly, Vector.COMPONENTS);
						}
						// all support vectors are calculated
						// calculate solution x and y components
						int sabovedist = cury - sabovey;
						int sbelowdist = sbelowy - cury;
						int sleftdist = curx - sleftx;
						int srightdist = srightx - curx;
						
						double solx1 	= (((double)(RNG_SPACING-sabovedist)/(RNG_SPACING*2)) * sabove.getX())
										+ (((double)(RNG_SPACING-sbelowdist)/(RNG_SPACING*2)) * sbelow.getX());
						double soly1 	= (((double)(RNG_SPACING-sabovedist)/(RNG_SPACING*2)) * sabove.getY())
										+ (((double)(RNG_SPACING-sbelowdist)/(RNG_SPACING*2)) * sbelow.getY());
						
						double solx2	= (((double)(RNG_SPACING-sleftdist)/(RNG_SPACING*2)) * sleft.getX())
										+ (((double)(RNG_SPACING-srightdist)/(RNG_SPACING*2)) * sright.getX());
						double soly2 	= (((double)(RNG_SPACING-sleftdist)/(RNG_SPACING*2)) * sleft.getY())
										+ (((double)(RNG_SPACING-srightdist)/(RNG_SPACING*2)) * sright.getY());
						
						double solx = solx1 + solx2;
						double soly = soly1 + soly2;
						// set the solution vector's components
						Vector sol = new Vector(solx, soly, Vector.COMPONENTS);
						this.setVector(curx, cury, sol);
					} else {
						// on xline and yline, this is an RNG vector
						// all of these are already set
					}
				}
				// the vector is sure to be calculated
				// move to the next vector in this row
				curx++;
			}
			// move to the first vector in the next row
			curx = 0;
			cury++;
		}
		
		// do nextDown rng vectors next
	}
}
