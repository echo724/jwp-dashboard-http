package nextstep.jwp.controller;

import nextstep.jwp.AbstractController;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.AuthenticationException;
import nextstep.jwp.model.User;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.header.Cookie;
import org.apache.coyote.http11.header.HttpStatus;

public class RegisterController extends AbstractController {
    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
        final var body = request.getBody();
        final String account = body.getValue("account");
        InMemoryUserRepository.findByAccount(account)
                .ifPresent(user -> {
                    throw new AuthenticationException("이미 존재하는 계정입니다.");
                });
        final String password = body.getValue("password");
        final String email = body.getValue("email");
        final User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        final SessionManager sessionManager = SessionManager.getInstance();
        final String sessionId = sessionManager.createSession(user);
        response.status(HttpStatus.FOUND)
                .location("/index.html")
                .setCookie(new Cookie("JSESSIONID", sessionId));
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        response.status(HttpStatus.FOUND)
                .location("/register.html");
    }
}
