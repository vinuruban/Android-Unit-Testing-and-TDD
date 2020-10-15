package com.techyourchance.mockitofundamentals.exercise5;

import com.techyourchance.mockitofundamentals.example7.eventbus.LoggedInEvent;
import com.techyourchance.mockitofundamentals.exercise5.eventbus.EventBusPoster;
import com.techyourchance.mockitofundamentals.exercise5.eventbus.UserDetailsChangedEvent;
import com.techyourchance.mockitofundamentals.exercise5.networking.NetworkErrorException;
import com.techyourchance.mockitofundamentals.exercise5.networking.UpdateUsernameHttpEndpointSync;
import com.techyourchance.mockitofundamentals.exercise5.users.User;
import com.techyourchance.mockitofundamentals.exercise5.users.UsersCache;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.awt.Event;
import java.util.List;

public class UpdateUsernameUseCaseSyncTest {

    public static final String USER_ID = "userId";
    public static final String USERNAME = "username";

    UpdateUsernameHttpEndpointSync updateUsernameHttpEndpointSyncMock;
    UsersCache usersCacheMock;
    EventBusPoster eventBusPosterMock;

    UpdateUsernameUseCaseSync SUT;

    @Before
    public void setUp() throws Exception {
        updateUsernameHttpEndpointSyncMock = mock(UpdateUsernameHttpEndpointSync.class);
        usersCacheMock = mock(UsersCache.class);
        eventBusPosterMock = mock(EventBusPoster.class);
        SUT = new UpdateUsernameUseCaseSync(updateUsernameHttpEndpointSyncMock, usersCacheMock, eventBusPosterMock);
        success();
    }

    /**  --- ENDPOINT CHECK --- **/

    /** if login succeeds, user ID and username should pass to the endpoint **/
    @Test
    public void updateUsernameSync_success_userIdAndUsernameToEndpoint() throws Exception {
        ArgumentCaptor<String> ac = ArgumentCaptor.forClass(String.class);
        SUT.updateUsernameSync(USER_ID, USERNAME);
        verify(updateUsernameHttpEndpointSyncMock, times(1)).updateUsername(ac.capture(), ac.capture());
        List<String> captures = ac.getAllValues();
        assertThat(captures.get(0), is(USER_ID));
        assertThat(captures.get(1), is(USERNAME));
    }


    /**  --- AUTH CACHE CHECK --- **/

    /** if login succeeds, user's auth should be cached **/
    @Test
    public void updateUsernameSync_success_userCached() throws Exception {
        ArgumentCaptor<User> ac = ArgumentCaptor.forClass(User.class);
        SUT.updateUsernameSync(USER_ID, USERNAME);
        verify(usersCacheMock).cacheUser(ac.capture());
        User cachedUser = ac.getValue();
        assertThat(cachedUser.getUserId(), is(USER_ID));
        assertThat(cachedUser.getUsername(), is(USERNAME));
    }

    /** if login fails with a general error, auth token shouldn't be taken **/
    @Test
    public void updateUsernameSync_generalError_userNotCached() throws Exception {
        generalError();
        SUT.updateUsernameSync(USER_ID, USERNAME);
        verifyNoMoreInteractions(usersCacheMock);
    }

    /** if login fails with an auth error, auth token shouldn't be taken **/
    @Test
    public void updateUsernameSync_authError_userNotCached() throws Exception {
        authError();
        SUT.updateUsernameSync(USER_ID, USERNAME);
        verifyNoMoreInteractions(usersCacheMock);
    }

    /** if login fails with a server error, auth token shouldn't be taken **/
    @Test
    public void updateUsernameSync_serverError_userNotCached() throws Exception {
        serverError();
        SUT.updateUsernameSync(USER_ID, USERNAME);
        verifyNoMoreInteractions(usersCacheMock);
    }

    /**  --- EVENT BUS CHECK --- **/
    /** if login succeeds, login event should be posted to event bus **/
    @Test
    public void updateUsernameSync_success_loggedInEventPosted() throws Exception {
        ArgumentCaptor<Object> ac = ArgumentCaptor.forClass(Object.class);
        SUT.updateUsernameSync(USER_ID, USERNAME);
        verify(eventBusPosterMock).postEvent(ac.capture());
        Object event = ac.getValue();
        assertThat(event, is(instanceOf(UserDetailsChangedEvent.class)));
    }
    /** if login fails with a general error, no login event should be posted to event bus **/
    @Test
    public void updateUsernameSync_generalError_noInteractionWithEventBusPoster() throws Exception {
        generalError();
        SUT.updateUsernameSync(USER_ID, USERNAME);
        verifyNoMoreInteractions(eventBusPosterMock);
    }

    /** if login fails with a auth error, no login event should be posted to event bus **/
    @Test
    public void updateUsernameSync_authError_noInteractionWithEventBusPoster() throws Exception {
        authError();
        SUT.updateUsernameSync(USER_ID, USERNAME);
        verifyNoMoreInteractions(eventBusPosterMock);
    }

    /** if login fails with a server error, no login event should be posted to event bus **/
    @Test
    public void updateUsernameSync_serverError_noInteractionWithEventBusPoster() throws Exception {
        serverError();
        SUT.updateUsernameSync(USER_ID, USERNAME);
        verifyNoMoreInteractions(eventBusPosterMock);
    }

    /**  --- RETURN STATUS CHECK --- **/

    /** if login succeeds, 'success' should be returned **/
    @Test
    public void updateUsernameSync_success_successReturned() throws Exception {
        UpdateUsernameUseCaseSync.UseCaseResult result = SUT.updateUsernameSync(USER_ID, USERNAME);
        assertThat(result, is(UpdateUsernameUseCaseSync.UseCaseResult.SUCCESS));
    }
    /** if login fails with a server error, 'failure' should be returned **/
    @Test
    public void updateUsernameSync_serverError_failureReturned() throws Exception {
        serverError();
        UpdateUsernameUseCaseSync.UseCaseResult result = SUT.updateUsernameSync(USER_ID, USERNAME);
        assertThat(result, is(UpdateUsernameUseCaseSync.UseCaseResult.FAILURE));
    }

    /** if login fails with a auth error, 'failure' should be returned **/
    @Test
    public void updateUsernameSync_authError_failureReturned() throws Exception {
        authError();
        UpdateUsernameUseCaseSync.UseCaseResult result = SUT.updateUsernameSync(USER_ID, USERNAME);
        assertThat(result, is(UpdateUsernameUseCaseSync.UseCaseResult.FAILURE));
    }

    /** if login fails with a general error, 'failure' should be returned **/
    @Test
    public void updateUsernameSync_generalError_failureReturned() throws Exception {
        generalError();
        UpdateUsernameUseCaseSync.UseCaseResult result = SUT.updateUsernameSync(USER_ID, USERNAME);
        assertThat(result, is(UpdateUsernameUseCaseSync.UseCaseResult.FAILURE));
    }

    /** if login fails with a network error, 'failure' should be returned **/
    @Test
    public void updateUsernameSync_networkError_failureReturned() throws Exception {
        networkError();
        UpdateUsernameUseCaseSync.UseCaseResult result = SUT.updateUsernameSync(USER_ID, USERNAME);
        assertThat(result, is(UpdateUsernameUseCaseSync.UseCaseResult.NETWORK_ERROR));
    }

    /**  --- HELPER METHODS --- **/

    private void success() throws NetworkErrorException {
        when(updateUsernameHttpEndpointSyncMock.updateUsername(anyString(), anyString()))
                .thenReturn(new UpdateUsernameHttpEndpointSync.EndpointResult(UpdateUsernameHttpEndpointSync.EndpointResultStatus.SUCCESS, USER_ID, USERNAME));
    }

    private void generalError() throws Exception {
        when(updateUsernameHttpEndpointSyncMock.updateUsername(anyString(), anyString()))
                .thenReturn(new UpdateUsernameHttpEndpointSync.EndpointResult(UpdateUsernameHttpEndpointSync.EndpointResultStatus.GENERAL_ERROR, "", ""));
    }

    private void authError() throws Exception {
        when(updateUsernameHttpEndpointSyncMock.updateUsername(anyString(), anyString()))
                .thenReturn(new UpdateUsernameHttpEndpointSync.EndpointResult(UpdateUsernameHttpEndpointSync.EndpointResultStatus.AUTH_ERROR, "", ""));
    }

    private void serverError() throws Exception {
        when(updateUsernameHttpEndpointSyncMock.updateUsername(anyString(), anyString()))
                .thenReturn(new UpdateUsernameHttpEndpointSync.EndpointResult(UpdateUsernameHttpEndpointSync.EndpointResultStatus.SERVER_ERROR, "", ""));
    }

    private void networkError() throws Exception {
        doThrow(new NetworkErrorException())
                .when(updateUsernameHttpEndpointSyncMock).updateUsername(anyString(), anyString());
    }

}