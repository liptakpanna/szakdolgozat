export async function checkJwtToken() {
        try {
            let response = await fetch(process.env.REACT_APP_API_URL + '/validate', {
                method: 'get',
                headers: {
                    "Authorization": 'Bearer ' + localStorage.getItem("jwtToken")
                }
            });

            let result = await response.json();
            console.log(result);
            if (result){
                localStorage.setItem("isLoggedIn", true);
                return result.valid;
            }
            else {
                alert("Something went wrong...");
            }
        }
        catch(e) {
            console.log(e)
        }
        logout();
        return false;
}

export function logout() {
    localStorage.setItem("isLoggedIn", false);
    localStorage.setItem("jwtToken", "");
    localStorage.setItem("username", "");
    localStorage.setItem("id", "");
    localStorage.setItem("role", "");
    console.log("Logged out");
}