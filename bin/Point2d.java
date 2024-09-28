class Point2d {
	private float x = 0.0f;
	private float y = 0.0f;
	//--- Constructors which prevents instance of class value from being badly modified. ---//
	Point2d() {
	
	}
	Point2d(float x, float y) {
		set(x, y);
	}
	Point2d(Point2d p) {
		this(p.x, p.y);
	}
	//--- Getter ---//
	float getX() {
		return x;
	}
	float getY() {
		return y;
	}
	//--- Setter ---//
	void setX(float x) {
		this.x = x;
	}
	void setY(float y) {
		this.y = y;
	}
	void set(float x, float y) {
		setX(x);
		setY(y);
	}
}