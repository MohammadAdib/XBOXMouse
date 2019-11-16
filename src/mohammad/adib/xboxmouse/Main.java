package mohammad.adib.xboxmouse;

import com.github.strikerx3.jxinput.XInputAxes;
import com.github.strikerx3.jxinput.XInputButtons;
import com.github.strikerx3.jxinput.XInputComponents;
import com.github.strikerx3.jxinput.XInputDevice;
import com.github.strikerx3.jxinput.enums.XInputButton;

import java.awt.*;
import java.awt.event.InputEvent;

public class Main {

    private static Robot robot;
    private static boolean a, b, x, active = true;

    public static void main(String[] args) {
        try {
            robot = new Robot();
            XInputDevice[] devices = XInputDevice.getAllDevices();
            for(XInputDevice device : devices) {
                if(device.poll()) {
                    useDevice(device);
                    break;
                }
            }
            System.out.println("No devices connected");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void useDevice(XInputDevice device) {
        XInputComponents components = device.getComponents();
        XInputButtons buttons = components.getButtons();
        XInputAxes axes = components.getAxes();
        System.out.println("\n------Welcome to XBOX Mouse-------");
        System.out.println("Left joystick: Move mouse");
        System.out.println("A: Left click");
        System.out.println("B: Right click");
        System.out.println("Right joystick: scroll");
        System.out.println("X: pause/play program");
        System.out.println();
        while (device.poll()) {
            Point pos = MouseInfo.getPointerInfo().getLocation();
            System.out.print(active ? "\rActive: (" + pos.x + ", " + pos.y + ")" : "\rPaused");
            pausePlay(buttons);
            if(active) {
                moveMouse(axes);
                clickMouse(buttons);
                dpadMouse(buttons);
                scrollMouse(axes);
            }
            sleep(10);
        }
        System.out.println("\rDone");
    }

    private static void moveMouse(XInputAxes axes) {
        Point mouseLocation = MouseInfo.getPointerInfo().getLocation();

        int dx = (int) (axes.lx * getMultiplier(axes.lx));
        int dy = (int) (axes.ly * getMultiplier(axes.ly));
        if(Math.abs(axes.lx) < 0.04) dx = 0;
        if(Math.abs(axes.ly) < 0.04) dy = 0;
        robot.mouseMove(mouseLocation.x + dx, mouseLocation.y - dy);
    }

    private static void clickMouse(XInputButtons buttons) {
        if(buttons.a != a) {
            if(buttons.a) {
                robot.mousePress(InputEvent.BUTTON1_MASK);
            } else {
                robot.mouseRelease(InputEvent.BUTTON1_MASK);
            }
        }
        if(buttons.b != b) {
            if(buttons.b) {
                robot.mousePress(InputEvent.BUTTON2_MASK);
            } else {
                robot.mouseRelease(InputEvent.BUTTON2_MASK);
            }
        }

        a = buttons.a;
        b = buttons.b;
    }

    private static void dpadMouse(XInputButtons buttons) {
        int dx = 0, dy = 0, speed = 3;
        if(buttons.up) {
            dy = speed;
        } else if(buttons.left) {
            dx = -speed;
        } else if(buttons.right) {
            dx = speed;
        } else if(buttons.down) {
            dy = -speed;
        }
        if(dx != 0 || dy != 0) {
            Point mouseLocation = MouseInfo.getPointerInfo().getLocation();
            robot.mouseMove(mouseLocation.x + dx, mouseLocation.y - dy);
        }
    }

    private static void pausePlay(XInputButtons buttons) {
        if(buttons.x != x && !buttons.x) active = !active;
        x = buttons.x;
    }

    private static void scrollMouse(XInputAxes axes) {
        int wheel = -(int) (axes.ry * 3f);
        if(wheel != 0) {
            System.out.print(" | Scroll wheel: " + wheel);
            robot.mouseWheel(wheel);
            sleep(100);
        }
    }

    private static float getMultiplier(float input) {
        float x = Math.abs(input);
        float A = 4.75f;
        float B = 8.69f;
        return (float) (A * (Math.pow(B, x)));
    }

    private static void sleep(int delay) {
        try {
            Thread.sleep(delay);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
