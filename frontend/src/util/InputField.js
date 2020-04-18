import React from 'react';

class InputField extends React.Component{

    render() {
        return(
            <div className="form-group">
                <label className='col-form-label'>{this.props.label}</label>
                <input 
                    className={"form-control " + (this.props.class ? this.props.class : "")}
                    type={this.props.type}
                    placeholder={this.props.placeholder}
                    value={this.props.value}
                    onChange={ (e) => this.props.onChange(e.target.value)}
                    maxLength={this.props.maxLength ? this.props.maxLength : "12"}
                    required={this.props.required ? this.props.required : false}
                />
            </div>
        );
    }
}

export default InputField;