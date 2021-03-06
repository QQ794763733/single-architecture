package cn.machine.geek.controller;

import cn.machine.geek.common.R;
import cn.machine.geek.dto.AuthorityTree;
import cn.machine.geek.entity.Authority;
import cn.machine.geek.security.CustomUserDetail;
import cn.machine.geek.service.AuthorityService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: MachineGeek
 * @Description: 权力控制器
 * @Email: 794763733@qq.com
 * @Date: 2021/1/7
 */
@Api(tags = "权力接口")
@RestController
@RequestMapping("/authority")
public class AuthorityController {
    @Autowired
    private AuthorityService authorityService;

    /**
    * @Author: MachineGeek
    * @Description: 获取权力树
    * @Date: 2021/1/11
     * @param
    * @Return: cn.machine.geek.common.R
    */
    @GetMapping("/tree")
    public R tree(){
        QueryWrapper<Authority> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().orderByDesc(Authority::getSort);
        return R.ok(getChild(0L,authorityService.list(queryWrapper)));
    }

    /**
    * @Author: MachineGeek
    * @Description: 获取当前用户的权限
    * @Date: 2021/1/11
     * @param
    * @Return: cn.machine.geek.common.R
    */
    @GetMapping("/getMyAuthorities")
    public R getMyAuthorities(Authentication authentication){
        // 获取当前用户ID
        CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();
        // 获取当前用户的所有权限
        List<Authority> authorities = authorityService.listByAccountId(customUserDetail.getId());
        // 把权限分为路由权限和API权限
        List<Authority> routes = new ArrayList<>();
        List<Authority> apis = new ArrayList<>();
        authorities.forEach((authority)->{
            if(authority.getUri() == null){
                apis.add(authority);
            }else{
                routes.add(authority);
            }
        });
        // 返回权限
        Map<String,Object> map = new HashMap<>();
        map.put("apis",apis);
        // 返回路由树
        map.put("routes",getChild(0L,routes));
        return R.ok(map);
    }

    /**
     * @Author: MachineGeek
     * @Description: 构建权限树
     * @Date: 2021/1/11
     * @param id
     * @param authorities
     * @Return: java.util.List<cn.machine.geek.dto.AuthorityTreeNode>
     */
    private List<AuthorityTree> getChild(Long id, List<Authority> authorities){
        List<AuthorityTree> child = new ArrayList<>();
        authorities.forEach((authority)->{
            if(authority.getPid().equals(id)){
                AuthorityTree authorityTree = new AuthorityTree();
                BeanUtils.copyProperties(authority,authorityTree);
                authorityTree.setChild(getChild(authorityTree.getId(),authorities));
                child.add(authorityTree);
            }
        });
        return child;
    }
}