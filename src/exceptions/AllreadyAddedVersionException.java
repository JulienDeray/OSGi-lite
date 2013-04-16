/**
 * @author julien
 */

package exceptions;


public class AllreadyAddedVersionException extends Exception {

    public AllreadyAddedVersionException(String message) {
        super(message);
    }
}
