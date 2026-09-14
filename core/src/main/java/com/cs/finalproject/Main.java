package com.cs.finalproject;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.utils.ScreenUtils;

/*
summary of oop:

class = blueprint
object = instance of class
encapsulation = hide data
inheritance = reuse/extend classes (hell)
polymorphism = same interface, diff behaviour (hell)
abstraction = hide complex stuff

interface = based on common BEHAVIOUR
abstract class = these things are basically the same (same same but different)
record = immutable
enum = god (possible states/values)
sealed class = control what can extend this class
final class = no inheriting this class


example of sealed, premits in use:
sealed class Animal permits Dog, Cat {}

final class Dog extends Animal {}
final class Cat extends Animal {}

sealed also works with interfaces:
sealed interface ... permits class1, class2... { ... }

use non-sealed to remove this (for stuff that extend sealed stuff)


summary of visibility:
                 same class   same package   subclass   everywhere
private              ✅            ❌            ❌          ❌
(default)            ✅            ✅            ❌          ❌
protected            ✅            ✅            ✅          ❌
public               ✅            ✅            ✅          ✅


composition:
instead of extending just keep an internal instance?
no:
class Car extends Engine {}

yes:
class Car {
    private Engine engine;
}

"car has an engine" vs "car is an engine"



factory pattern:
this is just super abstraction garbage

instead of:
Payment payment = new CreditCardPayment();

do:

class PaymentFactory {
    static Payment create(String type) {
        return switch (type) {
            case "credit" -> new CreditCardPayment();
            case "paypal" -> new PaypalPayment();
            default -> throw new IllegalArgumentException();
        };
    }
}

Payment payment = PaymentFactory.create("credit");

i guess this helps with controlling data (payment factory holds stuff internally, so you can add methods to do stuff to it)


dependency injection:

instead of:
class OrderService {
    private PaymentService payment = new PaymentService();
}

do:
class OrderService {
    private final PaymentService payment;

    OrderService(PaymentService payment) {
        this.payment = payment;
    }
}


why? literally no reason (except when using libraries...)


MOST IMPORTANTLY:
FAVOUR COMPOSITION OVER INHERITANCE PLEASEEEEE







alsoooo:
make sure to implement equals() and hashCode()

because what if:
User a = new User("Bob");
User b = new User("Bob");

a.equals(b); // should they be considered equal?

also:
the toString() method is called when printing an object
so you can modify it to print out certain stuff in a certain way


also:
static belongs to the class, not to instanced (and something about loading in memory at runtime something something...)

also:
upcasting is allowed, downcasting bad





this is pretty code too:
https://www.freecodecamp.org/news/introduction-to-solid-principles/



ALSO PLEASEEEE USE OPTIONALS AND RESULTS!!!








notes about intellij:
on the right panel there is like a turtle, that basically manages gradle tasks and stuff
to run application version:
    application -> run
to run web version:
    gdx-teavm -> gdx_teavm_web_js_run

NOTE: you should install the libgdx plugin via:
    hamburger menu top left -> settings -> plugins -> search libgdx and install and restart


intellij supports automatically creating:
- getters and setters
- other stuff
just right click on the method/class and click "generate"


note again: java is verbose as hell

 */

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture image;

    public PerspectiveCamera cam;
    public ModelBatch modelBatch;
    public Model model;
    public ModelInstance instance;

    @Override
    public void create() {
        modelBatch = new ModelBatch();
        batch = new SpriteBatch();
        image = new Texture("libgdx.png");

        cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(10f, 10f, 10f);
        cam.lookAt(0,0,0);
        cam.near = 1f;
        cam.far = 300f;
        cam.update();


        ModelBuilder modelBuilder = new ModelBuilder();
        model = modelBuilder.createBox(5f, 5f, 5f,
            new Material(ColorAttribute.createDiffuse(Color.GREEN)),
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        instance = new ModelInstance(model);

        //
    }

    @Override
    public void render() {
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        modelBatch.begin(cam);
        modelBatch.render(instance);
        modelBatch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        image.dispose();
        modelBatch.dispose();
        model.dispose();
    }
}
