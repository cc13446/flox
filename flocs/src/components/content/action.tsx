'use client'
import React, { useEffect, useState } from 'react';
import Fetch, { BodyType } from '../../lib/backend';

const App: React.FC = () => {

    const [action, setAction] = useState({});

    const loadAction = () => {
        Fetch({ path: '/data-source/action/select', method: 'get', bodyType: BodyType.FORM, data: {} }).then(a => {
            setAction(a);
        });
    }

    useEffect(() => {
        loadAction();
        return () => {
            setAction({});
        };
    }, []);



    return (
        <>
            <h1>Action</h1>
        </>
    );
};

export default App;