import React from 'react';
import Image from 'next/image'
import Logo from '../../public/logo.svg'
import ContentEnum from '../lib/contentEnum'

interface Props {
    setActiveContent: React.Dispatch<React.SetStateAction<ContentEnum>>
}

const App: React.FC<Props> = (props: Props) => {

    const setActiveContent = props.setActiveContent;

    return (
        <div className="w-full h-full border-2 flex flex-row justify-between items-center select-none">
            <div onClick={() => setActiveContent(ContentEnum.HEAD)} className="w-40 h-full pl-3 flex flex-row justify-between items-center select-none">
                <Image src={Logo} alt='logo' className='h-12' />
                <p className='font-bold text-3xl text-logo-color'>FLOX</p>
            </div>
            <div className='w-20 h-full flex flex-row justify-between items-center select-none'>
                <p className='font-bold select-none'><a href='https://github.com/cc13446/flox'>About</a></p>
            </div>
        </div>

    );
};

export default App;