package com.techyourchance.testdoublesfundamentals.example4;

import com.techyourchance.testdoublesfundamentals.example4.LoginUseCaseSync.UseCaseResult;
import com.techyourchance.testdoublesfundamentals.example4.authtoken.AuthTokenCache;
import com.techyourchance.testdoublesfundamentals.example4.eventbus.EventBusPoster;
import com.techyourchance.testdoublesfundamentals.example4.eventbus.LoggedInEvent;
import com.techyourchance.testdoublesfundamentals.example4.networking.LoginHttpEndpointSync;
import com.techyourchance.testdoublesfundamentals.example4.networking.NetworkErrorException;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class LoginUseCaseSyncTest {

    /** for testing purposes - can be empty too **/
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String AUTH_TOKEN = "authToken";
    public static final String NON_INITIALIZED_AUTH_TOKEN = "noAuthToken";

    /** to access the helper classes at the bottom of this class **/
    LoginHttpEndpointSyncTd mLoginHttpEndpointSyncTd;
    AuthTokenCacheTd mAuthTokenCacheTd;
    EventBusPosterTd mEventBusPosterTd;

    /** to trigger the method within the java class we're testing **/
    LoginUseCaseSync SUT;

    @Before
    public void setup() throws Exception {
        mLoginHttpEndpointSyncTd = new LoginHttpEndpointSyncTd();
        mAuthTokenCacheTd = new AuthTokenCacheTd();
        mEventBusPosterTd = new EventBusPosterTd();
        SUT = new LoginUseCaseSync(mLoginHttpEndpointSyncTd, mAuthTokenCacheTd, mEventBusPosterTd);
    }

    /** if login succeeds, username and password should pass to the endpoint **/
    @Test
    public void loginSync_success_usernameAndPasswordPassedToEndpoint() throws Exception {
        SUT.loginSync(USERNAME, PASSWORD);
        assertThat(mLoginHttpEndpointSyncTd.mUsername, is(USERNAME));
        assertThat(mLoginHttpEndpointSyncTd.mPassword, is(PASSWORD));
    }

    /** if login succeeds, user's auth should be cached **/
    @Test
    public void loginSync_success_authTokenCached() throws Exception {
        SUT.loginSync(USERNAME, PASSWORD);
        assertThat(mAuthTokenCacheTd.getAuthToken(), is(AUTH_TOKEN));
    }

    /** if login fails with a general error, auth token shouldn't be taken **/
    @Test
    public void loginSync_generalError_authTokenNotCached() throws Exception {
        mLoginHttpEndpointSyncTd.mIsGeneralError = true;
        SUT.loginSync(USERNAME, PASSWORD);
        assertThat(mAuthTokenCacheTd.getAuthToken(), is(NON_INITIALIZED_AUTH_TOKEN));
    }

    /** if login fails with an auth error, auth token shouldn't be taken **/
    @Test
    public void loginSync_authError_authTokenNotCached() throws Exception {
        mLoginHttpEndpointSyncTd.mIsAuthError = true;
        SUT.loginSync(USERNAME, PASSWORD);
        assertThat(mAuthTokenCacheTd.getAuthToken(), is(NON_INITIALIZED_AUTH_TOKEN));
    }

    /** if login fails with a server error, auth token shouldn't be taken **/
    @Test
    public void loginSync_serverError_authTokenNotCached() throws Exception {
        mLoginHttpEndpointSyncTd.mIsServerError = true;
        SUT.loginSync(USERNAME, PASSWORD);
        assertThat(mAuthTokenCacheTd.getAuthToken(), is(NON_INITIALIZED_AUTH_TOKEN));
    }

    /** if login succeeds, login event should be posted to event bus **/
    @Test
    public void loginSync_success_loggedInEventPosted() throws Exception {
        SUT.loginSync(USERNAME, PASSWORD);
        assertThat(mEventBusPosterTd.mEvent, is(instanceOf(LoggedInEvent.class)));
    }

    /** if login fails with a general error, no login event should be posted to event bus **/
    @Test
    public void loginSync_generalError_noInteractionWithEventBusPoster() throws Exception {
        mLoginHttpEndpointSyncTd.mIsGeneralError = true;
        SUT.loginSync(USERNAME, PASSWORD);
        assertThat(mEventBusPosterTd.mInteractionsCount, is(0));
    }

    /** if login fails with a auth error, no login event should be posted to event bus **/
    @Test
    public void loginSync_authError_noInteractionWithEventBusPoster() throws Exception {
        mLoginHttpEndpointSyncTd.mIsAuthError = true;
        SUT.loginSync(USERNAME, PASSWORD);
        assertThat(mEventBusPosterTd.mInteractionsCount, is(0));
    }

    /** if login fails with a server error, no login event should be posted to event bus **/
    @Test
    public void loginSync_serverError_noInteractionWithEventBusPoster() throws Exception {
        mLoginHttpEndpointSyncTd.mIsServerError = true;
        SUT.loginSync(USERNAME, PASSWORD);
        assertThat(mEventBusPosterTd.mInteractionsCount, is(0));
    }

    /** if login succeeds, 'success' should be returned **/
    @Test
    public void loginSync_success_successReturned() throws Exception {
        UseCaseResult result = SUT.loginSync(USERNAME, PASSWORD);
        assertThat(result, is(UseCaseResult.SUCCESS));
    }

    /** if login fails with a server error, 'failure' should be returned **/
    @Test
    public void loginSync_serverError_failureReturned() throws Exception {
        mLoginHttpEndpointSyncTd.mIsServerError = true;
        UseCaseResult result = SUT.loginSync(USERNAME, PASSWORD);
        assertThat(result, is(UseCaseResult.FAILURE));
    }

    /** if login fails with a auth error, 'failure' should be returned **/
    @Test
    public void loginSync_authError_failureReturned() throws Exception {
        mLoginHttpEndpointSyncTd.mIsAuthError = true;
        UseCaseResult result = SUT.loginSync(USERNAME, PASSWORD);
        assertThat(result, is(UseCaseResult.FAILURE));
    }

    /** if login fails with a general error, 'failure' should be returned **/
    @Test
    public void loginSync_generalError_failureReturned() throws Exception {
        mLoginHttpEndpointSyncTd.mIsGeneralError = true;
        UseCaseResult result = SUT.loginSync(USERNAME, PASSWORD);
        assertThat(result, is(UseCaseResult.FAILURE));
    }

    /** if login fails with a network error, 'failure' should be returned **/
    @Test
    public void loginSync_networkError_networkErrorReturned() throws Exception {
        mLoginHttpEndpointSyncTd.mIsNetworkError = true;
        UseCaseResult result = SUT.loginSync(USERNAME, PASSWORD);
        assertThat(result, is(UseCaseResult.NETWORK_ERROR));
    }

    // ---------------------------------------------------------------------------------------------
    // Helper classes - CONSTRUCTORS!

    private static class LoginHttpEndpointSyncTd implements LoginHttpEndpointSync {

        /** to store data and access these in the tests **/
        public String mUsername = "";
        private String mPassword = "";

        /** triggered from the tests **/
        public boolean mIsGeneralError;
        public boolean mIsAuthError;
        public boolean mIsServerError;
        public boolean mIsNetworkError;

        @Override
        public EndpointResult loginSync(String username, String password) throws NetworkErrorException {

            /** to store data and access these in the tests **/
            mUsername = username;
            mPassword = password;

            /** triggered from the tests **/
            if (mIsGeneralError) {
                return new EndpointResult(EndpointResultStatus.GENERAL_ERROR, "");
            } else if (mIsAuthError) {
                return new EndpointResult(EndpointResultStatus.AUTH_ERROR, "");
            }  else if (mIsServerError) {
                return new EndpointResult(EndpointResultStatus.SERVER_ERROR, "");
            } else if (mIsNetworkError) {
                throw new NetworkErrorException();
            } else {
                return new EndpointResult(EndpointResultStatus.SUCCESS, AUTH_TOKEN);
            }
        }
    }

    private static class AuthTokenCacheTd implements AuthTokenCache {

        String mAuthToken = NON_INITIALIZED_AUTH_TOKEN;

        @Override
        public void cacheAuthToken(String authToken) {
            mAuthToken = authToken;
        }

        @Override
        public String getAuthToken() {
            return mAuthToken;
        }
    }

    private static class EventBusPosterTd implements EventBusPoster {
        public Object mEvent;
        public int mInteractionsCount;

        @Override
        public void postEvent(Object event) {
            mInteractionsCount++;
            mEvent = event;
        }
    }
}