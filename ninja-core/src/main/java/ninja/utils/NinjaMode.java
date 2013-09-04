package ninja.utils;

public enum NinjaMode {

    prod(NinjaConstant.MODE_PROD), 
    dev(NinjaConstant.MODE_DEV), 
    test(NinjaConstant.MODE_TEST);

    private String mode;

    NinjaMode(String mode) {
        this.mode = mode;
    }

    public String toString() {
        return mode;
    }

}
