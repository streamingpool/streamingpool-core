/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.util;

import static java.util.Objects.requireNonNull;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Contains utility methods to serialize and deserialize objects from/to files. This uses the standard
 * serializion/deserializion mechanism and is intended to be used mainly for testing.
 * 
 * @author kfuchsbe
 */
public final class FileSerializations {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileSerializations.class);

    private FileSerializations() {
        /* Only static methods */
    }

    /**
     * Serializes the given object into the given path.
     * 
     * @param object the object to serialize
     * @param filePath the path to where to write the file to. The parent dir must exist.
     */
    public static void serializeToFile(Object object, String filePath) {
        requireNonNull(object, "object must not be null.");
        requireNonNull(filePath, "filePath must not be null.");

        try (FileOutputStream fout = new FileOutputStream(filePath, true);
                ObjectOutputStream oos = new ObjectOutputStream(fout);) {
            oos.writeObject(object);
            oos.close();
        } catch (IOException e) {
            throw new RuntimeException(String.format("Could not write %s from file '%s'.", object, filePath), e);
        }
        LOGGER.info("Successfully wrote {} to '{}'", object, filePath);
    }

    /**
     * Deserializes the object from the geven file path and casts it to the given type.
     * 
     * @param typeToLoad the type which is expected inside the class
     * @param filePath the path to the file from which to read the object
     * @return the object from the given class
     */
    public static final <T> T deserializeFromFile(Class<T> typeToLoad, String filePath) {
        requireNonNull(typeToLoad, "typeToLoad must not be null");
        requireNonNull(filePath, "filePath must not be null");
        try (FileInputStream streamIn = new FileInputStream(filePath);
                ObjectInputStream objectinputstream = new ObjectInputStream(streamIn);) {
            T object = typeToLoad.cast(objectinputstream.readObject());
            objectinputstream.close();
            LOGGER.info("Successfully read {} from '{}'.", object, filePath);
            return object;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(
                    "Could not load object of type " + typeToLoad.getName() + " from file '" + filePath + "'.", e);
        }
    }
}
