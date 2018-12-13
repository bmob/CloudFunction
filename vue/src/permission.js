import Vue from 'vue'

import router from './router'
import store from './store'
import {
    Message
} from 'element-ui'
import NProgress from 'nprogress' // progress bar
import 'nprogress/nprogress.css' // progress bar style

NProgress.configure({
    showSpinner: false
}) // NProgress Configuration

const whiteList = ['/login'] // no redirect whitelist

var currentRole = {
    loadedVisitor: false,
};

function startsWith(s1, s2) {
    if (s1 && s2 && s1.length >= s2.length)
        return s1.substring(0, s2.length) == s2;
    return false;
}

router.beforeEach((to, from, next) => {
    NProgress.start() // start progress bar
    if (false) { // determine if there has token
        /* has token*/
        if (to.path === '/login') {
            next({
                path: '/'
            })
            NProgress.done() // if current page is dashboard will not trigger	afterEach hook, so manually handle it
        } else {
            // var roleSelect = Vue.prototype.$GetRole();
            var roleSelect = null;
            if (roleSelect && roleSelect.role) {
                if (!roleSelect || !roleSelect.role || (currentRole.role == roleSelect.role && currentRole.permission == roleSelect.permission)) {
                    next();
                } else {
                    console.log('更新权限');
                    store.dispatch('GenerateRoutes', {
                        role: roleSelect.role,
                        permission: roleSelect.permission,
                    }).then(() => { // 根据roles权限生成可访问的路由表
                        currentRole.role = roleSelect.role;
                        currentRole.permission = roleSelect.permission;
                        currentRole.loadedVisitor = false;

                        router.addRoutes(store.getters.addRouters) // 动态添加可访问路由表
                        next({ ...to,
                            replace: true
                        }) // hack方法 确保addRoutes已完成 ,set the replace: true so the navigation will not leave a history record
                    });
                }
            } else {
                console.log('Role data:', roleSelect);
                var _next = function() {
                    if (startsWith(to.path, '/mine') || startsWith(to.path, '/company') || to.path == '/dashboard') {
                        next();
                    } else {
                        console.log('Cannot go ' + to.path + ', select company first.');
                        Vue.prototype.$Toast(Vue.prototype.str('company.noRole'));
                        console.log('goto company select');
                        next({
                            path: '/company'
                        })
                        NProgress.done();
                    }
                }
                if (currentRole.loadedVisitor)
                    _next();
                else {
                    store.dispatch('GenerateRoutes', {
                        role: 0,
                        permission: 0,
                    }).then(() => { // 根据roles权限生成可访问的路由表
                        currentRole.loadedVisitor = true;

                        router.addRoutes(store.getters.addRouters) // 动态添加可访问路由表
                        next({ ...to,
                            replace: true
                        }) // hack方法 确保addRoutes已完成 ,set the replace: true so the navigation will not leave a history record
                    });
                }
            }

            // if (store.getters.roles.length === 0) { // 判断当前用户是否已拉取完user_info信息
            //     store.dispatch('GetInfo').then(res => { // 拉取user_info
            //         console.log(res.data.roles)
            //         const roles = res.data.roles // note: roles must be a array! such as: ['editor','develop']
            //         store.dispatch('GenerateRoutes', {
            //             roles
            //         }).then(() => { // 根据roles权限生成可访问的路由表
            //             router.addRoutes(store.getters.addRouters) // 动态添加可访问路由表
            //             next({ ...to,
            //                 replace: true
            //             }) // hack方法 确保addRoutes已完成 ,set the replace: true so the navigation will not leave a history record
            //         })
            //     }).catch((err) => {
            //         store.dispatch('FedLogOut').then(() => {
            //             Message.error(err || 'Verification failed, please login again')
            //             next({
            //                 path: '/'
            //             })
            //         })
            //     })
            // } else {
            //     next()
            // }
        }
    } else {
        /* has no token*/
        if (whiteList.indexOf(to.path) !== -1) { // 在免登录白名单，直接进入
            next()
        } else {
            next(`/login?redirect=${to.path}`) // 否则全部重定向到登录页
            NProgress.done()
        }
    }
})

router.afterEach(() => {
    NProgress.done() // finish progress bar
})