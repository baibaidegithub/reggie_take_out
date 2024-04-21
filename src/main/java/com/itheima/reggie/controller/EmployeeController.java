package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;


    /**
     * 员工登陆
     *
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    //@RequestBody接收Json数据 HttpServletRequest是需要将员工登陆信息存到session当中
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        /*
         * 1。将页面提交的密码password进行md5加密处理
         * 2。根据页面提交的用户名username查询数据库
         * 3。如果没有查询到则返回登陆失败结果
         * 4。密码比对，如果不一致则返回登陆失败结果
         * 5。查看员工状态，如果为已禁用状态，则返回员工已禁用结果
         * 6。登陆成功，将员工id存入Session并返回登陆成功结果
         * */

        //1。将页面提交的密码password进行md5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //2。根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        //3。如果没有查询到则返回登陆失败结果
        if (emp == null) {
            return R.error("登陆失败");
        }

        //4。密码比对，如果不一致则返回登陆失败结果
        if (!emp.getPassword().equals(password)) {
            return R.error("登陆失败");
        }

        //5。查看员工状态，如果为已禁用状态，则返回员工已禁用结果
        if (emp.getStatus() == 0) {
            return R.error("账号已禁用");
        }

        //6。登陆成功，将员工id存入Session并返回登陆成功结果
        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);
    }


    /**
     * 员工退出
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    //需要操作session，将request对象取出来
    public R<String> logout(HttpServletRequest request) {
        //清理session中保存的当前登陆员工的id
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    /**
     * 新增员工
     *
     * @param request
     * @param employee
     * @return
     */
    @PostMapping
    //因为数据以json形式传过来，所以要加一个@RequestBody
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee) {
        log.info("新增员工，员工信息：{}", employee.toString());
        //设置初始密码123456，需要进行md5加密处理
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        //设置创建时间
        //employee.setCreateTime(LocalDateTime.now());
        //设置更新时间
        //employee.setUpdateTime(LocalDateTime.now());
        //获得当前登录用户的id
        //Long empid = (Long) request.getSession().getAttribute("employee");
        //employee.setCreateUser(empid);
        //employee.setUpdateUser(empid);
        employeeService.save(employee);

        return R.success("新增员工成功");
    }

    /**
     * 员工信息分页查询
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        log.info("page={},pageSize={},name={}", page, pageSize, name);
        //构造分页构造器
        Page pageInfo = new Page(page,pageSize);

        //构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();
        //添加过滤条件
        queryWrapper.like(StringUtils.isNotBlank(name),Employee::getName,name);
        //添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        //执行查询
        employeeService.page(pageInfo,queryWrapper);

        return R.success(pageInfo);
    }

    /**
     * 根据id修改员工信息
     * @param request
     * @param employee
     * @return
     */
    @PutMapping
    //@RequestBody来获取json格式的employee数据
    //HttpServletRequest获取session内容
    public R<String> update(HttpServletRequest request, @RequestBody Employee employee){
        log.info(employee.toString());
        long id = Thread.currentThread().getId();
        log.info("线程id为：{}",id);
        //获取session中的userId
        //Long empId = (Long) request.getSession().getAttribute("employee");
        //更新更新时间
        //employee.setUpdateTime(LocalDateTime.now());
        //employee.setUpdateUser(empId);
        //使用id进行查找
        employeeService.updateById(employee);
        return  R.success("员工信息修改成功！");
    }


    /**
     * 根据id来查询员工信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    //加@PathVariable可以将参数加入请求路径中
    public R<Employee> getId(@PathVariable Long id){
        log.info("根据id查询员工信息");
        Employee employee = employeeService.getById(id);
        if(employee!=null){
            return R.success(employee);
        }
        return R.error("没有查询到对应员工信息");
    }


}
