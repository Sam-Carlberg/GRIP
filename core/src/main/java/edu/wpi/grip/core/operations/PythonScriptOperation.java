package edu.wpi.grip.core.operations;

import edu.wpi.grip.core.Operation;
import edu.wpi.grip.core.OperationDescription;
import edu.wpi.grip.core.sockets.InputSocket;
import edu.wpi.grip.core.sockets.OutputSocket;
import edu.wpi.grip.core.sockets.SocketHint;
import edu.wpi.grip.core.util.Icons;
import org.python.core.Py;
import org.python.core.PyException;
import org.python.core.PyFunction;
import org.python.core.PyObject;
import org.python.core.PySequence;
import org.python.core.PyString;
import org.python.core.PySystemState;
import org.python.util.PythonInterpreter;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * A class that implements an operation using Jython.  This enables users to write plugins for the application as
 * Python scripts.  Python script plugins should have global lists of SocketHints called "inputs" and "outputs" that
 * declare what parameters the script accepts and what outputs in produces. For example,
 *
 * <pre>{@code
 *    import edu.wpi.grip.core as grip
 *    import java.lang.Integer
 *
 *    inputs = [
 *        grip.SocketHint("a", java.lang.Integer, grip.SocketHint.View.SLIDER, (0, 100), 75),
 *        grip.SocketHint("b", java.lang.Integer, grip.SocketHint.View.SLIDER, (0, 100), 25),
 *    ]
 *
 *    outputs = [
 *        grip.SocketHint("c", java.lang.Integer),
 *    ]
 * }</pre>
 *
 * The script should also define a function "perform", which takes the same number of parameters as there are inputs
 * and returns the values for the outputs.  It can return a single value if there's one output, or a sequence type for
 * any number of values.
 *
 * <pre>{@code
 * def perform(a, b):
 * return a + b
 * }</pre>
 *
 * Lastly, the script can optionally have global "name" and "description" strings to provide the user with more
 * information about what the operation does.
 */
public class PythonScriptOperation implements Operation {

    static {
        Properties pythonProperties = new Properties();
        pythonProperties.setProperty("python.import.site", "false");
        PySystemState.initialize(pythonProperties, null);
    }

    private static final String DEFAULT_NAME = "Python Operation";
    private static final String DEFAULT_DESCRIPTION = "";
    private static final Logger logger =  Logger.getLogger(PythonScriptOperation.class.getName());


    // Either a URL or a String of literal source code is stored in this field.  This allows a PythonScriptOperation to
    // be serialized as a reference to some code rather than trying to save a bunch of Jython internal structures to a
    // file, which is what would automatically happen otherwise.
    private final Optional<URL> sourceURL;
    private final Optional<String> sourceCode;

    private final PythonInterpreter interpreter = new PythonInterpreter();

    private InputSocket.Factory isf;
    private OutputSocket.Factory osf;

    private List<SocketHint<PyObject>> inputSocketHints;
    private List<SocketHint<PyObject>> outputSocketHints;
    private List<InputSocket<?>> inputSockets;
    private List<OutputSocket<?>> outputSockets;
    private PyFunction performFunction;
    private PyString name;
    private PyString description;

    public PythonScriptOperation(InputSocket.Factory isf, OutputSocket.Factory osf, URL url) throws PyException, IOException {
        this.isf = checkNotNull(isf);
        this.osf = checkNotNull(osf);
        this.sourceURL = Optional.of(url);
        this.sourceCode = Optional.empty();
        this.interpreter.execfile(url.openStream());
        this.getPythonVariables();

        if (this.name == null) {
            // If a name of the operation wasn't specified in the script, use the basename of the URL
            final String path = url.getPath();
            this.name = new PyString(path.substring(1 + Math.max(path.lastIndexOf("/"), path.lastIndexOf("\\"))));
        }

        if (this.description == null) {
            this.description = new PyString(DEFAULT_DESCRIPTION);
        }

        this.inputSockets = inputSocketHints.stream()
                .map(isf::create)
                .collect(Collectors.toList());

        this.outputSockets = outputSocketHints.stream()
                .map(osf::create)
                .collect(Collectors.toList());
    }

    public PythonScriptOperation(InputSocket.Factory isf, OutputSocket.Factory osf, String code) throws PyException {
        this.isf = checkNotNull(isf);
        this.osf = checkNotNull(osf);
        this.sourceURL = Optional.empty();
        this.sourceCode = Optional.of(code);
        this.interpreter.exec(code);
        this.getPythonVariables();

        if (this.name == null) {
            this.name = new PyString(DEFAULT_NAME);
        }

        if (this.description == null) {
            this.description = new PyString(DEFAULT_DESCRIPTION);
        }

        this.inputSockets = inputSocketHints.stream()
                .map(isf::create)
                .collect(Collectors.toList());

        this.outputSockets = outputSocketHints.stream()
                .map(osf::create)
                .collect(Collectors.toList());
    }

    private void getPythonVariables() throws PyException {
        this.inputSocketHints = this.interpreter.get("inputs", List.class);
        this.outputSocketHints = this.interpreter.get("outputs", List.class);
        this.performFunction = this.interpreter.get("perform", PyFunction.class);
        this.name = this.interpreter.get("name", PyString.class);
        this.description = this.interpreter.get("description", PyString.class);
    }

    public OperationDescription getDescription() {
        return OperationDescription.builder()
                .name(this.name.getString())
                .description(this.description.getString())
                .icon(Icons.iconStream("python"))
                .category(OperationDescription.Category.MISCELLANEOUS)
                .build();
    }

    public Optional<URL> getSourceURL() {
        return this.sourceURL;
    }

    public Optional<String> getSourceCode() {
        return this.sourceCode;
    }

    /**
     * @return An array of Sockets, based on the global "inputs" list in the Python script
     */
    @Override
    public InputSocket<?>[] createInputSockets() {
        return inputSockets.toArray(new InputSocket[0]);
    }

    /**
     * @return An array of Sockets, based on the global "outputs" list in the Python script
     */
    @Override
    public OutputSocket<?>[] createOutputSockets() {
        return outputSockets.toArray(new OutputSocket[0]);
    }

    /**
     * Perform the operation by calling a function in the Python script.
     * <p>
     * This method adapts each of the inputs into Python objects, calls the Python function, and then converts the
     * outputs of the function back into Java objects and assigns them to the outputs array.
     * <p>
     * The Python function should return a tuple, list, or other sequence containing the outputs.  If there is only
     * one output, it can just return a value.  Either way, the number of inputs and outputs should match up with the
     * number of parameters and return values of the function.
     */
    @Override
    public void perform() {
        InputSocket[] inputs = createInputSockets();
        OutputSocket[] outputs = createOutputSockets();
        PyObject[] pyInputs = new PyObject[inputs.length];
        for (int i = 0; i < inputs.length; i++) {
            pyInputs[i] = Py.java2py(inputs[i].getValue().get());
        }

        try {
            PyObject pyOutput = this.performFunction.__call__(pyInputs);

            if (pyOutput.isSequenceType()) {
                /* If the Python function returned a sequence type, there must be multiple outputs for this step.
                 * Each element in the sequence is assigned to one output socket. */
                PySequence pySequence = (PySequence) pyOutput;
                Object[] javaOutputs = Py.tojava(pySequence, Object[].class);

                if (outputs.length != javaOutputs.length) {
                    throw new RuntimeException(wrongNumberOfArgumentsMsg(outputs.length, javaOutputs.length));
                }

                for (int i = 0; i < javaOutputs.length; i++) {
                    outputs[i].setValue(javaOutputs[i]);
                }
            } else {
                /* If the Python script did not return a sequence, there should only be one output socket. */
                if (outputs.length != 1) {
                    throw new RuntimeException(wrongNumberOfArgumentsMsg(outputs.length, 1));
                }

                Object javaOutput = Py.tojava(pyOutput, outputs[0].getSocketHint().getType());
                outputs[0].setValue(javaOutput);
            }
        } catch (Exception e) {
            /* Exceptions can happen if there's a mistake in a Python script, so just print a stack trace and leave the
             * current state of the output sockets alone.
             *
             * TODO: communicate the error to the GUI.
             */
            logger.log(Level.WARNING, e.getMessage(), e);
        }
    }

    private static String wrongNumberOfArgumentsMsg(int expected, int actual) {
        return "Wrong number of outputs from Python script (expected " + expected + ", got " + actual + ")";
    }
}
