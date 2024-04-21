import React from 'react';
import {
    ApartmentOutlined, ApiOutlined, ControlOutlined, DatabaseOutlined, DeploymentUnitOutlined,
    ForkOutlined, SlidersOutlined, SwapOutlined
} from '@ant-design/icons';
import type { MenuProps } from 'antd';
import { Menu } from 'antd';

type MenuItem = Required<MenuProps>['items'][number];

function getItem(
    label: React.ReactNode,
    key: React.Key,
    icon?: React.ReactNode,
    children?: MenuItem[],
    type?: 'group',
): MenuItem {
    return {
        key,
        icon,
        children,
        label,
        type,
    } as MenuItem;
}

const items: MenuProps['items'] = [
    getItem('数据源', 'dataSource', <DatabaseOutlined />, [
        getItem('连接配置', 'connect', <ApiOutlined />),
        getItem('动作配置', 'action', <ControlOutlined />)
    ]),

    { type: 'divider' },

    getItem('数据流', 'flox', <SlidersOutlined />, [
        getItem('数据类型定义', 'dataType', <DeploymentUnitOutlined />),
        getItem('节点定义', 'node', <SwapOutlined />),
        getItem('子流程配置', 'subFlox', <ForkOutlined />),
        getItem('流程配置', 'flox', <ApartmentOutlined />),
    ]),

    { type: 'divider' },

];

const App: React.FC = () => {
    return (
        <Menu
            style={{ width: 256 }}
            mode="inline"
            items={items}
        />
    );
};

export default App;