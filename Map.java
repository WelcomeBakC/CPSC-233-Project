
import java.util.ArrayList;
import java.lang.Math;

public class Map{
    private ArrayList<BasicGameObject> basicObjList = new ArrayList<BasicGameObject>();
    private ArrayList<DynamicGameObject> dynamicObjList = new ArrayList<DynamicGameObject>();
    private ArrayList<Driver> driverList = new ArrayList<Driver>();

    private int width;
    private int height;

	/**
	 * Constructor takes in driverList of type Driver as well as integers
	 * width and height and uses a constructor within the class to initialize
	 * the values given values.
	 */
    public Map(ArrayList<Driver> driverList, int width, int height){
        this(null, null, driverList, width, height);

    }

	/**
	 * full constructor
	 */
    public Map(
	    ArrayList<BasicGameObject> basicObjList,
	    ArrayList<DynamicGameObject> dynamicObjList,
	    ArrayList<Driver> driverList, int width, int height) {

        if (basicObjList != null) {
            this.basicObjList.addAll(basicObjList);

        }
        if (dynamicObjList != null) {
            this.dynamicObjList.addAll(dynamicObjList);

        }
        this.driverList.addAll(driverList);
        this.width = width;
        this.height = height;

    }

    /**
	 * method returns list of basic objects
	 * list contains non-moving obstacles and walls
	 */
    public ArrayList<BasicGameObject> getBasicObjList() {
        return basicObjList;

    }

    /**
	 * method returns list of dynamic objects
	 * list contains cars and moving obstacles
	 */
    public ArrayList<DynamicGameObject> getDynamicObjList() {
        return dynamicObjList;

    }

    /**
	 * returns exact list of drivers
	 */
    public ArrayList<Driver> getDriverList() {
      return driverList;

    }

    /**
	 * method returns width
	 */
    public int getWidth() {
        return width;

    }

    /**
	 * method returns height
	 */
    public int getHeight() {
        return height;

    }

    /**
	 * Add given basic object to the BasicGameObject list
	 */
    public void addBasicGameObject(BasicGameObject s1) {
        this.basicObjList.add(s1);

    }

    /**
	 * Add given dynamic object to the DynamicGameObject list
	 */
    public void addDynamicGameObject(DynamicGameObject d1) {
        this.dynamicObjList.add(d1);

    }

	/**
	 *
	 */
    public void giveInput(ArrayList<Character> character, double time) {
        for (Driver i: driverList) {
            i.takeInput(character, time);

        }
    }

	/**
	 *
	 */
  	public void giveInput(String input, double time) {
        ArrayList<Character> inputInChar = new ArrayList<Character>();

        for (int i = 0; i < input.length(); i++) {
            inputInChar.add(Character.valueOf(input.charAt(i)));

        }
        giveInput(inputInChar, time);

    }

	/**
	 *
	 */
    public ArrayList<BasicGameObject> getProximityObjects(DynamicGameObject d, double proximity) {
        ArrayList<BasicGameObject> withinProximity = new ArrayList<BasicGameObject>();

        for (BasicGameObject o: basicObjList) {
            if (o.distance(d) <= proximity) {
            withinProximity.add(o);

            }
        }
        return withinProximity;

    }

    /**
	 * Method will detect collisions using the Axis Align Bounding-Boxes (AABB).
	 * Method takes in a DynamicGameObject named dObj and an array list of type BasicGameObject sObjs.
	 *
	 * Creates a new array list for potential collisions. Then iterates through the given list
	 * using a for loop. Check if the maximum radius (maxR) is less than the total length of x and
	 * maximum raidus of the basic object.
	 *
	 * If all conditions in the if statement are satisfied, the basic object, "s", that would potentially
	 * collide with the dObj is added into type BasicGameObject  array list "potentialCollisions".
	 */
    public ArrayList<BasicGameObject> detectAABB(DynamicGameObject dObj,
        ArrayList<? extends BasicGameObject> sObjs){

        ArrayList<BasicGameObject> potentialCollisions = new ArrayList<BasicGameObject>();

        for (BasicGameObject s : sObjs){
            if (dObj.getMaxR() < s.getX() + s.getMaxR() &&
                dObj.getMaxR() + s.getX() > s.getX() &&
                dObj.getMaxR() < s.getY() + s.getMaxR() &&
                dObj.getY() + s.getMaxR() > s.getY()){

                potentialCollisions.add(s);

            }
        }
        return potentialCollisions;
    }

    /**
	 * test if two objects b (moving object) and a are colliding using the
	 * Separating Axis Theorem (SAT)
	 *
	 * resource: Separating Axis Theorem for Oriented Bounding Boxes by Johnny Huynh www.jkh.me
	 *
	 * @return true if colliding, false if not
	 */
    public boolean testSAT(DynamicGameObject b, BasicGameObject a) {
        double tx, ty;
        double cD = Math.cos(b.getDirection());
        double sD = Math.sin(b.getDirection());

        if (a instanceof Wall) {
        	// Case 1: a is a Wall
            Wall a_ = (Wall) a;

            double adx = a_.getEndX() - a_.getStartX();
            double ady = a_.getEndY() - a_.getStartY();
            double dx = b.getX() - a_.getStartX();
            double dy = b.getY() - a_.getStartY();
            double dx2 = b.getX() - a_.getEndX();
            double dy2 = b.getY() - a_.getEndY();
            double p = ((dx*adx) + (dy*ady)) / (adx*adx + ady*ady);

            if (Math.abs(a_.getEndX() - a_.getStartX()) == Math.abs(p * adx) + Math.abs(a_.getEndX() - a_.getStartX() - p * adx) &&
                Math.abs(a_.getEndY() - a_.getStartY()) == Math.abs(p * ady) + Math.abs(a_.getEndY() - a_.getStartY() - p * ady)) {
            	// Case 1.1: closest point to b's centre lies on the wall

                tx = dx - p*adx;
                ty = dy - p*ady;

                return !(Math.abs(ty * adx - tx * ady) > a_.getHalfW() * Math.sqrt(ady * ady + adx * adx)
                    + Math.abs(b.getHalfW() * (sD * ady + cD * adx))
                    + Math.abs(b.getHalfH() * (sD * adx - cD * ady)) ||
                    Math.abs(ty * cD - tx * sD) > b.getHalfW()
                    + Math.abs(a_.getHalfH() * (ady * cD - adx * sD)) ||
                    Math.abs(tx * cD + ty * sD) > b.getHalfH()
                    + Math.abs(a_.getHalfH() * (adx * cD + ady * sD)));

            }else {
            	// Case 1.2: closest point to b's centre is an endpoint of the wall
                if (Math.sqrt(dx * dx + dy * dy) <= Math.sqrt(dx2 * dx2 + dy2 * dy2)) {
                    tx = dx;
                    ty = dy;

                }else {
                    tx = dx2;
                    ty = dy2;

                }
                return !(Math.abs(ty * adx - tx * ady) >
                    Math.abs(b.getHalfW() * (sD * ady + cD * adx))
                    + Math.abs(b.getHalfH() * (sD * adx - cD * ady)) ||
                    Math.abs(tx * adx + ty * ady) >
                    Math.abs(b.getHalfW() * (cD * ady - sD * adx))
                    + Math.abs(b.getHalfH() * (cD * adx + sD * ady)) ||
                    Math.abs(ty * cD - tx * sD) > b.getHalfW() ||
                    Math.abs(tx * cD + ty * sD) > b.getHalfH());

            }
        }else {
        	// Case 2: a is a rectangle with no direction
            tx = a.getX() - b.getX();
            ty = a.getY() - b.getY();

            return !(Math.abs(tx) > a.getHalfW()
                + Math.abs(b.getHalfW() * -sD)
                + Math.abs(b.getHalfH() * cD) ||
                Math.abs(ty) > a.getHalfH()
                + Math.abs(b.getHalfW() * cD)
                + Math.abs(b.getHalfH() * sD) ||
                Math.abs(ty * cD - tx * sD) > b.getHalfW()
                + Math.abs(a.getHalfW() * -sD)
                + Math.abs(a.getHalfH() * cD) ||
                Math.abs(tx * cD + ty * sD) > b.getHalfH()
                + Math.abs(a.getHalfW() * cD)
                + Math.abs(a.getHalfH() * sD));

        }
    }

    /**
	 * performs SAT tests between given DynamicGameObject and every object in list
	 *
	 * @return list of objects that are colliding with dObj
	 */
    public ArrayList<BasicGameObject> detectSATCollisions(DynamicGameObject dObj,
        ArrayList<? extends BasicGameObject> sObjs){

        ArrayList<BasicGameObject> colliding = new ArrayList<BasicGameObject>();

        for (BasicGameObject o: sObjs) {
            if (dObj != o && testSAT(dObj, o)) {
                colliding.add(o);

            }
        }
        return colliding;

    }

    /**
     * performs collision testing between every DynamicGameObject and all other objects
     * resolves all detected collisions
     */
    public void collisionDetectResolveAll() {
    	for (DynamicGameObject o: dynamicObjList) {
    		for (BasicGameObject s: detectSATCollisions(o, basicObjList)) {
    			s.resolveCollision(o);

    		}
    		for (BasicGameObject d: detectSATCollisions(o, dynamicObjList)) {
    			d.resolveCollision(o);

    		}
    	}
    }

    /**
     * apply flag action to basic object o
     */
    public void handleFlag(BasicGameObject o, Flag f){
        switch (f.toString()) {
            case ("DESTROY"):
                basicObjList.remove(o);
                break;

        }
    }

    /**
     * apply flag action to dynamic object o
     */
    public void handleFlag(DynamicGameObject o, Flag f) {
    	switch (f.toString()) {
            case ("DESTROY"):
                dynamicObjList.remove(o);
                break;

            case ("ADD_SPEED"):
                o.setSpeed(o.getSpeed() + f.getValue());
                break;

            case ("SET_SPEED"):
                o.setSpeed(f.getValue());
                break;

            case ("ADD_DIRECTION"):
                o.setDirection(o.getDirection() + f.getValue());
                break;

            case ("SET_DIRECTION"):
                o.setDirection(f.getValue());
                break;
        }

        if (o instanceof Car) {
        	Driver i = null;

        	for (Driver i_: driverList) {
        		if (i_.getAttachedCar() == o) {
        			i = i_;
        		}
        	}

        	if (i != null) {
        		switch (f.toString()) {
            		case ("NEXT_SECTION"):
            			if (i.getSection() == f.getValue() - 1) {
            				i.setSection((int) f.getValue());

            			}
            			break;

    				case ("NEXT_LAP"):
    					if (i.getSection() == f.getValue()) {
    						i.setSection(0);
    						i.setLap(i.getLap() + 1);
    					}
    					break;
            	}
        	}
        }
    }

    /**
	 * handle all flags of each object and tick all DynamicGameObjects to change positions
	 */
    public ArrayList<BasicGameObject> tickAll(double time){
    	ArrayList<BasicGameObject> toUpdate = new ArrayList<BasicGameObject>();

        for (BasicGameObject o: new ArrayList<BasicGameObject>(basicObjList)) {
        	if (!o.getFlags().isEmpty()) {
				for (Flag f: o.getFlags()) {
		        	handleFlag(o, f);
		        	o.removeFlag(f);

		        }
	            toUpdate.add(o);

        	}
        }

        for (DynamicGameObject o: new ArrayList<DynamicGameObject>(dynamicObjList)) {
        	if (!o.getFlags().isEmpty()) {
				for (Flag f: o.getFlags()) {
		        	handleFlag(o, f);
		        	o.removeFlag(f);

		        }
        	}
            toUpdate.add(o);
        	o.tick(time);

        }

        return toUpdate;

    }
}
