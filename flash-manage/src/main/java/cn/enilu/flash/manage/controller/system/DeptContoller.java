package cn.enilu.flash.manage.controller.system;

import cn.enilu.flash.common.bean.vo.front.Rets;
import cn.enilu.flash.common.aop.BussinessLog;
import cn.enilu.flash.common.bean.entity.system.Dept;
import cn.enilu.flash.common.bean.exception.ApplicationException;
import cn.enilu.flash.common.bean.vo.node.DeptNode;
import cn.enilu.flash.common.enumeration.BizExceptionEnum;
import cn.enilu.flash.common.enumeration.Permission;
import cn.enilu.flash.common.log.LogObjectHolder;
import cn.enilu.flash.common.service.system.DeptService;
import cn.enilu.flash.common.utils.BeanUtil;
import cn.enilu.flash.manage.controller.BaseController;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * DeptContoller
 *
 * @author enilu
 * @version 2018/9/15 0015
 */
@RestController
@RequestMapping("/dept")
public class DeptContoller extends BaseController {
    private Logger logger = LoggerFactory.getLogger(MenuController.class);

    @Autowired
    private DeptService deptService;

    @GetMapping(value = "/list")
    @RequiresPermissions(value = {Permission.DEPT})
    public Object list() {
        List<DeptNode> list = deptService.queryAllNode();
        return Rets.success(list);
    }

    @PostMapping
    @BussinessLog(value = "编辑部门", key = "simplename")
    @RequiresPermissions(value = {Permission.DEPT_EDIT})
    public Object save(@RequestBody @Valid Dept dept) {
        if (BeanUtil.isOneEmpty(dept, dept.getSimplename())) {
            throw new ApplicationException(BizExceptionEnum.REQUEST_NULL);
        }
        if (dept.getId() != null) {
            Dept old = deptService.get(dept.getId());
            LogObjectHolder.me().set(old);
            old.setPid(dept.getPid());
            old.setSimplename(dept.getSimplename());
            old.setFullname(dept.getFullname());
            old.setNum(dept.getNum());
            deptService.deptSetPids(old);
            deptService.update(old);
        } else {
            deptService.deptSetPids(dept);
            deptService.insert(dept);
        }
        return Rets.success();
    }

    @DeleteMapping
    @BussinessLog(value = "删除部门", key = "id")
    @RequiresPermissions(value = {Permission.DEPT_DEL})
    public Object remove(@RequestParam Long id) {
        logger.info("id:{}", id);
        if (id == null) {
            throw new ApplicationException(BizExceptionEnum.REQUEST_NULL);
        }
        deptService.deleteDept(id);
        return Rets.success();
    }
}
