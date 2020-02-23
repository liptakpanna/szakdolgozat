import {extendObservable} from 'mobx';

class UserStore {
    constructor() {
        extendObservable(this, {

            loading: true,
            isLoggedIn: false,
            username: '',
            jwtToken: ''
        })
    }
}

export default new UserStore();