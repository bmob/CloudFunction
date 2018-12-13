import {
    asyncRouterMap,
    constantRouterMap
} from '@/router'

/**
 * 通过meta.role判断是否与当前用户权限匹配
 * @param roles
 * @param route
 */
function hasPermission(route, role, permission) {
    return route.meta &&
        (!route.meta.role || role === route.meta.role || (route.meta.role.indexOf && route.meta.role.indexOf(role) != -1)) &&
        (!route.meta.permission || ((permission & route.meta.permission) != 0));
    // return route.meta 
    //     && (!route.meta.role || role === route.meta.role)
    //     && (!route.meta.permission || ((permission & route.meta.permission) != 0));
}

/**
 * 递归过滤异步路由表，返回符合用户角色权限的路由表
 * @param routes asyncRouterMap
 * @param roles
 */
function filterAsyncRouter(routes, role, permission) {
    const res = []

    routes.forEach(route => {
        const tmp = { ...route
        };
        if (hasPermission(tmp, role, permission)) {
            if (tmp.children)
                tmp.children = filterAsyncRouter(tmp.children, role, permission);
            res.push(tmp)
        }
    })

    return res
}

const permission = {
    state: {
        routers: constantRouterMap,
        addRouters: []
    },
    mutations: {
        SET_ROUTERS: (state, routers) => {
            state.addRouters = routers
            state.routers = constantRouterMap.concat(routers)

            console.log("Routers:", state.routers);
        }
    },
    actions: {
        GenerateRoutes({
            commit
        }, roleData, ) {
            return new Promise(resolve => {
                let accessedRouters = filterAsyncRouter(asyncRouterMap, roleData.role, roleData.permission);
                commit('SET_ROUTERS', accessedRouters)
                resolve()
            })
        }
    }
}

export default permission