import React from 'react';

class PreviousPageIcon extends React.Component{

    onClick(e) {
        this.props.hist.push(this.props.where);
    }

    render() {
        return(
            <div>
                <i className="previousIcon" onClick={(e) => this.onClick(e)}></i>
            </div>
        )
    }

}

export default PreviousPageIcon;
