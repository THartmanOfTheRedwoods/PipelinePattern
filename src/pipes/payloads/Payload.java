package pipes.payloads;

public class Payload {
    private Object result = null;

    public <T> Payload(Class<T> desiredType, Object result) {
        this.setResult(desiredType, result);
    }
    public <T> T getResult(Class<T> desiredType) {
        if (this.result.getClass().isAssignableFrom(desiredType)) {
            return desiredType.cast(this.result);
        } else {
            throw new IllegalArgumentException();
        }
    }

    public <T> void setResult(Class<T> desiredType, Object result) {
        if (result.getClass().isAssignableFrom(desiredType)) {
            this.result = desiredType.cast(result);
        } else {
            throw new IllegalArgumentException();
        }
    }

}
