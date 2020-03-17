import React from 'react';

class InputField extends React.Component{

    render() {
        let additionalClass = this.props.class ? this.props.class : "";
        let maxLength = this.props.maxLength ? this.props.maxLength : "12";
        return(
            <div className="form-group">
                <label className='col-form-label'>{this.props.label}</label>
                <input 
                    className={"form-control " + additionalClass}
                    type={this.props.type}
                    placeholder={this.props.placeholder}
                    value={this.props.value}
                    onChange={ (e) => this.props.onChange(e.target.value)}
                    maxLength={maxLength}
                />
            </div>
        );
    }
}

export default InputField;