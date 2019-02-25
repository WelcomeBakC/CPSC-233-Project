import java.util.ArrayList;
import java.lang.Math;

public class Interface{
	private Car attachedCar;

	public Interface(Car car){
		attachedCar = car;
	}

	private void accelerate(double time){
		attachedCar.setSpeed(attachedCar.getSpeed() + attachedCar.getBaseAcceleration() * time);
	}

	private void brake(double time){
		double v = attachedCar.getSpeed();

		if(v > 0){
			v -= attachedCar.getBaseAcceleration() * time * 1.5;
		}else{
			v += attachedCar.getBaseAcceleration() * time * 0.3;
		}

		attachedCar.setSpeed(v);
	}

	private void turn(boolean clockwise, double time){
		if(clockwise){
			attachedCar.setAngularVelocity(Math.toRadians(10));
		}else{
			attachedCar.setAngularVelocity(Math.toRadians(-10));
		}
	}

	public void takeInput(ArrayList<Character> input, double time){
		for(char c: input){
			switch(c){
				case('w'):
					accelerate(time);
					break;
				case('s'):
					brake(time);
					break;
				case('a'):
					turn(false, time);
					break;
				case('d'):
					turn(true, time);
					break;
			}
		}
	}
}
