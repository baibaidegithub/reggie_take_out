package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.AddressBook;
import com.itheima.reggie.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 地址簿管理
 */
@Slf4j
@RestController
@RequestMapping("/addressBook")
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;

//    /**
//     * 新增
//     */
//    @PostMapping
//    public R<AddressBook> save(@RequestBody AddressBook addressBook) {
//        addressBook.setUserId(BaseContext.getCurrentId());
//        log.info("addressBook:{}", addressBook);
//        addressBookService.save(addressBook);
//        return R.success(addressBook);
//    }
//
//    /**
//     * 设置默认地址
//     */
//    @PutMapping("default")
//    public R<AddressBook> setDefault(@RequestBody AddressBook addressBook) {
//        log.info("addressBook:{}", addressBook);
//        LambdaUpdateWrapper<AddressBook> wrapper = new LambdaUpdateWrapper<>();
//        wrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId());
//        wrapper.set(AddressBook::getIsDefault, 0);
//        //SQL:update address_book set is_default = 0 where user_id = ?
//        addressBookService.update(wrapper);
//
//        addressBook.setIsDefault(1);
//        //SQL:update address_book set is_default = 1 where id = ?
//        addressBookService.updateById(addressBook);
//        return R.success(addressBook);
//    }
//
//    /**
//     * 根据id查询地址
//     */
//    @GetMapping("/{id}")
//    public R get(@PathVariable Long id) {
//        AddressBook addressBook = addressBookService.getById(id);
//        if (addressBook != null) {
//            return R.success(addressBook);
//        } else {
//            return R.error("没有找到该对象");
//        }
//    }
//
//    /**
//     * 查询默认地址
//     */
//    @GetMapping("default")
//    public R<AddressBook> getDefault() {
//        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId());
//        queryWrapper.eq(AddressBook::getIsDefault, 1);
//
//        //SQL:select * from address_book where user_id = ? and is_default = 1
//        AddressBook addressBook = addressBookService.getOne(queryWrapper);
//
//        if (null == addressBook) {
//            return R.error("没有找到该对象");
//        } else {
//            return R.success(addressBook);
//        }
//    }
//
//    /**
//     * 查询指定用户的全部地址
//     */
//    @GetMapping("/list")
//    public R<List<AddressBook>> list(AddressBook addressBook) {
//        addressBook.setUserId(BaseContext.getCurrentId());
//        log.info("addressBook:{}", addressBook);
//
//        //条件构造器
//        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(null != addressBook.getUserId(), AddressBook::getUserId, addressBook.getUserId());
//        queryWrapper.orderByDesc(AddressBook::getUpdateTime);
//
//        //SQL:select * from address_book where user_id = ? order by update_time desc
//        return R.success(addressBookService.list(queryWrapper));
//    }


    /**
     * 展示地址列表
     * @param addressBook
     * @return
     */
    //这里虽然前端没有传入参数，但是在函数里写这个AddressBook addressBook等于在方法体第一行写AddressBook addressBook = new AddressBook();
    //这样的好处是，允许 list 方法具有一定的灵活性。如果将来需要根据其他属性（例如地址类型）过滤地址信息，可以在前端传递相应的参数，并在后端相应地修改查询条件。
    @GetMapping("/list")
    public R<List<AddressBook>> list(AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(null != addressBook.getUserId(), AddressBook::getUserId, addressBook.getUserId());
        queryWrapper.orderByDesc(AddressBook::getUpdateTime);
        List<AddressBook> data = addressBookService.list(queryWrapper);
        return R.success(data);
    }

    /**
     * 新增地址
     * @param addressBook
     * @return
     */
    @PostMapping
    public R<AddressBook> save(@RequestBody AddressBook addressBook){
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBookService.save(addressBook);
        return R.success(addressBook);
    }

    /**
     * 更新地址
     * @param addressBook
     * @return
     */
    @PutMapping
    public R<AddressBook> update(@RequestBody AddressBook addressBook){
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getId,addressBook.getId());
        addressBookService.update(addressBook,queryWrapper);
        return R.success(addressBook);
    }


    /**
     * 修改地址时回显
     * @param id
     * @return
     */
    //这里R后面不跟类型代表返回一个范型，既可以返回AddressBook类型，也可以返回String类型的error信息。
    @GetMapping("/{id}")
    public R search(@PathVariable Long id){
        AddressBook addressBook = addressBookService.getById(id);
        if(addressBook!=null){
            return R.success(addressBook);
        }else {
            return R.error("找不到该条数据！");
        }
    }

    /**
     * 设为默认地址
     * @param addressBook
     * @return
     */
    //LambdaUpdateWrapper用于更新操作，LambdaQueryWrapper用于查询操作
    @PutMapping("/default")
    public R<AddressBook> setDefault(@RequestBody AddressBook addressBook){
        LambdaUpdateWrapper<AddressBook> queryWrapper = new LambdaUpdateWrapper<>();
        queryWrapper.eq(AddressBook::getUserId,BaseContext.getCurrentId());
        queryWrapper.set(AddressBook::getIsDefault,0);
        addressBookService.update(queryWrapper);
        addressBook.setIsDefault(1);
        addressBookService.updateById(addressBook);
        return R.success(addressBook);
    }

    /**
     * 获取默认地址
     * @return
     */
    @GetMapping("/default")
    public R<AddressBook> getDefault(){
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId,BaseContext.getCurrentId());
        queryWrapper.eq(AddressBook::getIsDefault,1);
        AddressBook addressBook = addressBookService.getOne(queryWrapper);
        if(addressBook==null){
            return R.error("没有默认地址！");
        }else {
            return R.success(addressBook);
        }
    }
}
