package com.yjs.service.authority;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.yjs.dao.authority.MenuDao;
import com.yjs.entity.PageResult;
import com.yjs.pojo.Menu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service(interfaceClass = MenuService.class)
public class MenuServiceImpl implements MenuService {

    @Override
    //查询user对象对应的菜单
    public List<Menu> selectByUser(String username) {
        List<Menu> menus = menuDao.selectByUser(username);
        return menus;
    }

    @Autowired
    private MenuDao menuDao;
    
    @Override
    //根据表单提交的数据新增菜单
    @Transactional
    public void add(Menu menu) {
        setLevel(menu);
        menuDao.add(menu);
    }

    @Override
    //进行分页查询
    public PageResult pageQuery(Integer currentPage, Integer pageSize, String queryString) {
        //基于Mybatis分页助手插件实现分页
        PageHelper.startPage(currentPage,pageSize);
        Page<Menu> page = menuDao.selectByCondition(queryString);
        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    //根据id删除指定菜单
    public void delete(Integer id) {
        //查询当前菜单是否和检查组关联
        long count = menuDao.findCountByMenuId(id);
        if(count > 0){
            //当前菜单被引用，不能删除，抛出异常并设置提示信息，供controller层捕获
            throw new RuntimeException("当前菜单被引用，不能删除");
        }
        //根据id删除指定菜单
        menuDao.deleteById(id);
    }

    @Override
    //以传入的check对象进行数据更新
    public void edit(Menu menu) {
        setLevel(menu);
        //调用dao层进行更新数据
        menuDao.edit(menu);
    }

    @Override
    //根据指定id查询检查菜单
    public Menu findById(Integer id) {
        return menuDao.findById(id);
    }

    @Override
    //查询所有菜单
    public List<Menu> findAll() {
        return menuDao.findAll();
    }

    @Override
    //查询所有level为1的菜单的id
    public List<Map> selectParentMenuIds() {
        return menuDao.selectParentMenuIds();
    }
    
   //设置菜单对象的level属性
    public Menu setLevel(Menu menu) {
        //判断父id是否为0，如果否，则将等级设置为2，否则为1
        if (menu.getParentMenuId()!=0){
            menu.setLevel(2);
        }else {
            menu.setLevel(1);
        }
        return menu;
    }
}
