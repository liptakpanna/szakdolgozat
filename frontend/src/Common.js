import React from 'react';
import Modal from 'react-bootstrap/Modal';
import SubmitButton from './SubmitButton';

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

export function Confirm(task) {
    var show= false;
  
    const handleClose = () => {show = false};
    const handleShow = () => {show = true};
  
    return (
      <>
        <SubmitButton variant="primary" onClick={handleShow}>
          Launch demo modal
        </SubmitButton>
  
        <Modal show={show} onHide={handleClose}>
          <Modal.Header closeButton>
            <Modal.Title>Are you sure you want to delete it?</Modal.Title>
          </Modal.Header>
          <Modal.Body>Deleting this object cannot be undone.</Modal.Body>
          <Modal.Footer>
            <SubmitButton className=" btn-secondary" onClick={handleClose}>
              Close
            </SubmitButton>
            <SubmitButton className=" btn-danger" onClick={task}>
              Delete
            </SubmitButton>
          </Modal.Footer>
        </Modal>
      </>
    );
  }