import {extendObservable} from 'mobx';

class UserStore {
    constructor() {
        extendObservable(this, {
            loading: true,
            username: 'new',
            jwtToken: '',
            role: '',
            id: '',
            isLoggedIn: false
        })
    }

    reset() {
        this.username= '';
        this.jwtToken = '';
        this.role = '';
        this.id = '';
        this.isLoggedIn = false;
    }
}

export default new UserStore();