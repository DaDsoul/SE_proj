import {RegistrationApprove, Schedule, AdminLogin, Login} from './ActionToState';
import { createStore, combineReducers, applyMiddleware } from 'redux';
import thunk from 'redux-thunk';
import logger from 'redux-logger';

export const configureStore = () => {
    const store = createStore(
        combineReducers({
            schedule: Schedule,
            registrationApproveState: RegistrationApprove,
            loginUser: Login,
            admin: AdminLogin
        }),
        applyMiddleware(thunk, logger)
    );
    return store
};