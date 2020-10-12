package com.techyourchance.testdoublesfundamentals.exercise4;

import com.techyourchance.testdoublesfundamentals.example4.LoginUseCaseSync;
import com.techyourchance.testdoublesfundamentals.example4.networking.NetworkErrorException;
import com.techyourchance.testdoublesfundamentals.exercise4.networking.UserProfileHttpEndpointSync;
import com.techyourchance.testdoublesfundamentals.exercise4.users.User;
import com.techyourchance.testdoublesfundamentals.exercise4.users.UsersCache;

import org.jetbrains.annotations.Nullable;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.techyourchance.testdoublesfundamentals.exercise4.FetchUserProfileUseCaseSync.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class FetchUserProfileUseCaseSyncTest {

    /** test data - can be empty too **/
    public static final String USER_ID = "userId";
    public static final String FULL_NAME = "fullName";
    public static final String IMAGE_URL = "imageUrl";

    /** to access the helper classes at the bottom of this class **/
    ImplementedFromUserProfileHttpEndpointSync newUserProfileHttpEndpointSync;
    ImplementedFromUsersCache newUsersCache;

    /** to trigger the method within the java class we're testing **/
    FetchUserProfileUseCaseSync SUT;

    @Before
    public void setUp() throws Exception {
        /** below is one way to call in the classes (implemented from interfaces) directly, but we will need to initialise them to access them outside of this @Before method **/
//        SUT = new FetchUserProfileUseCaseSync(new UserProfileHttpEndpointSync() {
//            @Override
//            public EndpointResult getUserProfile(String userId) throws NetworkErrorException {
//                return null;
//            }
//        }, new UsersCache() {
//            @Override
//            public void cacheUser(User user) {
//
//            }
//
//            @Nullable
//            @Override
//            public User getUser(String userId) {
//                return null;
//            }
//        });
        newUserProfileHttpEndpointSync = new ImplementedFromUserProfileHttpEndpointSync();
        newUsersCache = new ImplementedFromUsersCache();
        SUT = new FetchUserProfileUseCaseSync(newUserProfileHttpEndpointSync, newUsersCache);
    }

    /** if login succeeds, user ID should pass to the endpoint **/
    @Test
    public void fetchUserProfileSync_success_userIdToEndpoint() {
        SUT.fetchUserProfileSync(USER_ID);
        assertThat(newUserProfileHttpEndpointSync.mUserId, is(USER_ID));
    }

    /** if login succeeds, user should be cached **/
    @Test
    public void fetchUserProfileSync_success_userCached() {
        SUT.fetchUserProfileSync(USER_ID);
        User user = newUsersCache.getUser(USER_ID);
        assertThat(user.getUserId(), is(USER_ID));
        assertThat(user.getFullName(), is(FULL_NAME));
        assertThat(user.getImageUrl(), is(IMAGE_URL));
    }

    /** if login succeeds, 'success' should be returned **/
    @Test
    public void fetchUserProfileSync_success_successReturned() {
        UseCaseResult result = SUT.fetchUserProfileSync(USER_ID);
        assertThat(result, is(UseCaseResult.SUCCESS));
    }

    /** if login fails with a server error, 'failure' should be returned **/
    @Test
    public void loginSync_serverError_failureReturned() throws Exception {
        newUserProfileHttpEndpointSync.mIsServerError = true;
        UseCaseResult result = SUT.fetchUserProfileSync(USER_ID);
        assertThat(result, is(UseCaseResult.FAILURE));
    }

    /** if login fails with an auth error, 'failure' should be returned **/
    @Test
    public void loginSync_authError_failureReturned() throws Exception {
        newUserProfileHttpEndpointSync.mIsAuthError = true;
        UseCaseResult result = SUT.fetchUserProfileSync(USER_ID);
        assertThat(result, is(UseCaseResult.FAILURE));
    }

    /** if login fails with a general error, 'failure' should be returned **/
    @Test
    public void loginSync_generalError_failureReturned() throws Exception {
        newUserProfileHttpEndpointSync.mIsGeneralError = true;
        UseCaseResult result = SUT.fetchUserProfileSync(USER_ID);
        assertThat(result, is(UseCaseResult.FAILURE));
    }

    /** if login fails with a general error, user shouldn't cached **/
    @Test
    public void loginSync_generalError_noUserCached() throws Exception {
        newUserProfileHttpEndpointSync.mIsGeneralError = true;
        SUT.fetchUserProfileSync(USER_ID);
        User user = newUsersCache.getUser(USER_ID);
        assertThat(user, is(nullValue()));
    }

    /** if login fails with a auth error, user shouldn't cached **/
    @Test
    public void loginSync_authError_noUserCached() throws Exception {
        newUserProfileHttpEndpointSync.mIsAuthError = true;
        SUT.fetchUserProfileSync(USER_ID);
        User user = newUsersCache.getUser(USER_ID);
        assertThat(user, is(nullValue()));
    }

    /** if login fails with a server error, user shouldn't cached **/
    @Test
    public void loginSync_serverError_noUserCached() throws Exception {
        newUserProfileHttpEndpointSync.mIsServerError = true;
        SUT.fetchUserProfileSync(USER_ID);
        User user = newUsersCache.getUser(USER_ID);
        assertThat(user, is(nullValue()));
    }

    /** if login fails with a network error, 'failure' should be returned **/
    @Test
    public void loginSync_networkError_networkErrorReturned() throws Exception {
        newUserProfileHttpEndpointSync.mIsNetworkError = true;
        UseCaseResult result = SUT.fetchUserProfileSync(USER_ID);
        assertThat(result, is(UseCaseResult.NETWORK_ERROR));
    }


    // ---------------------------------------------------------------------------------------------
    // Helper classes - implemented from INTERFACES!

    private static class ImplementedFromUserProfileHttpEndpointSync implements UserProfileHttpEndpointSync {

        /** to store data and access this in the tests **/
        public String mUserId = "";

        /** triggered from the tests **/
        public boolean mIsGeneralError;
        public boolean mIsAuthError;
        public boolean mIsServerError;
        public boolean mIsNetworkError;

        @Override
        public EndpointResult getUserProfile(String userId) throws NetworkErrorException {

            /** to store data and access this in the tests **/
            mUserId = userId;

            /** triggered from the tests **/
            if (mIsGeneralError) {
                return new EndpointResult(EndpointResultStatus.GENERAL_ERROR, "", "", "");
            } else if (mIsAuthError) {
                return new EndpointResult(EndpointResultStatus.AUTH_ERROR, "", "", "");
            }  else if (mIsServerError) {
                return new EndpointResult(EndpointResultStatus.SERVER_ERROR, "", "", "");
            } else if (mIsNetworkError) {
                throw new NetworkErrorException();
            } else {
                return new EndpointResult(EndpointResultStatus.SUCCESS, USER_ID, FULL_NAME, IMAGE_URL);
            }
        }

    }

    private static class ImplementedFromUsersCache implements UsersCache {

        private List<User> mUsers = new ArrayList<>(1); /** since 'User user' below has many fields within, we will create a list **/

        @Override
        public void cacheUser(User user) {
            User existingUser = getUser(user.getUserId());
            if (existingUser != null) {
                mUsers.remove(existingUser); /** delete previous user in 'user'... **/
            }
            mUsers.add(user); /** ...and then new saver **/
        }

        @Nullable
        @Override
        public User getUser(String userId) {
            for (User user : mUsers) {
                if (user.getUserId().equals(userId)) {
                    return user;
                }
            }
            return null;
        }

    }


}