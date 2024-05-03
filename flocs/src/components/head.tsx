import React from 'react';
import Image from 'next/image'
import Logo from '../../public/logo.svg'


const App: React.FC = () => {
    return (
        <div className="w-full h-16 border-2 flex flex-row justify-between items-center">

            <div className="w-40 h-full pl-3 flex flex-row justify-between items-center">
                <Image src={Logo} alt='logo' className='h-12' />
                <p className='font-bold text-3xl text-logo-color'>FLOX</p>
            </div>
            <div className='w-20 h-full flex flex-row justify-between items-center'>
            <p className='font-bold'>About</p>
            </div>
        </div>

    );
};

export default App;