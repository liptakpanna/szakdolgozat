import UserStore from './store/UserStore';

export async function checkJwtToken() {
        try {
            let response = await fetch(process.env.REACT_APP_API_URL + '/validate', {
                method: 'get',
                headers: {
                    "Authorization": 'Bearer ' + UserStore.jwtToken
                }
            });

            let result = await response.json();
            console.log(result);
            if (result){
                return result.valid;
            }
            else {
                alert("Something went wrong...");
            }
        }
        catch(e) {
            console.log(e)
        }
        return false;
}