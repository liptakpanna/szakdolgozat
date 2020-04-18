import Cookie from "js-cookie"

export async function checkJwtToken() {
        try {
            let response = await fetch(process.env.REACT_APP_API_URL + '/validate', {
                method: 'get',
                headers: {
                    "Authorization": 'Bearer ' + Cookie.get("jwtToken")
                }
            })

            let result = await response.json();
            console.log(result);
            if (result != null){
                if(result === false || result.status === 403)
                    logout();
                return result;
            }
            else {
                alert("Something went wrong.")
            }
        }
        catch(e) {
            logout();
            console.log("Cannot connect to server. " + e);
            return false;
        }
        return false;
}

export function logout() {
    Cookie.set("jwtToken", null);
    localStorage.setItem("username", "");
    localStorage.setItem("id", "");
    localStorage.setItem("role", "");
    console.log("Logged out");
}

export function validateEmail (email) {
    const regexp = /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    return regexp.test(email);
}