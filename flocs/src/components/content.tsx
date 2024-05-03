import React from 'react';


interface Props {
    activeContent: String,
}

const App: React.FC<Props> = (props: Props) => {
    return (
        <div className=''>
            <p>{props.activeContent}</p>
        </div>
    );
};

export default App;