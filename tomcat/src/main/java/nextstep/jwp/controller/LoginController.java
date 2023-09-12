package nextstep.jwp.controller;

import nextstep.jwp.AbstractController;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.AuthenticationException;
import nextstep.jwp.model.User;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.body.Body;
import org.apache.coyote.http11.header.Cookie;
import org.apache.coyote.http11.header.HttpStatus;

public class LoginController extends AbstractController {
    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
        final Body body = request.getBody();
        final String account = body.getValue("account");
        final String password = body.getValue("password");
        final User loginUser = InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .orElseThrow(() -> new AuthenticationException("아이디 또는 비밀번호가 틀립니다."));
        final SessionManager sessionManager = SessionManager.getInstance();
        final String sessionId = sessionManager.createSession(loginUser);
        response.status(HttpStatus.FOUND)
                .location("/index.html")
                .setCookie(new Cookie("JSESSIONID", sessionId));
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        if (request.getHeaders().getCookies().containsKey("JSESSIONID")) {
            response.status(HttpStatus.FOUND)
                    .location("/index.html");
            return;
        }
        response.status(HttpStatus.FOUND)
                .location("/login.html");
    }
}
