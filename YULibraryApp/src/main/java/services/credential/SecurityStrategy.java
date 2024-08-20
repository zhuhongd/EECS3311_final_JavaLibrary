package services.credential;

public interface SecurityStrategy {
    boolean validate(IAuthToken token);

    IAuthToken generate(ICredentials credentials);
}
